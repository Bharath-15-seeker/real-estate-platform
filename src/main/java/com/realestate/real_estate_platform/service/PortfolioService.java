package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.dto.PortfolioDTO;
import com.realestate.real_estate_platform.entity.Portfolio;
import com.realestate.real_estate_platform.entity.Property;
import com.realestate.real_estate_platform.entity.User;
import com.realestate.real_estate_platform.repositories.ContactRepository;
import com.realestate.real_estate_platform.repositories.PortfolioRepository;
import com.realestate.real_estate_platform.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final ContactRepository contactRepository;

    private final UserRepository userRepository;
    private final PortfolioRepository portfolioRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public void createPortfolio(Portfolio portfolio, MultipartFile dp, MultipartFile[] images) {
        List<String> imagePaths = new ArrayList<>();

        // Ensure upload directory exists
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }

        // ✅ Save Display Picture (if provided)
        if (dp != null && !dp.isEmpty()) {
            try {
                String filename = UUID.randomUUID() + "_" + dp.getOriginalFilename();
                File destinationFile = new File(uploadPath, filename);
                dp.transferTo(destinationFile);

                // Store relative path for frontend
                portfolio.setDp("/uploads/" + filename);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // ✅ Save multiple work images
        if (images != null) {
            for (MultipartFile image : images) {
                try {
                    String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                    File destinationFile = new File(uploadPath, filename);
                    image.transferTo(destinationFile);

                    imagePaths.add("/uploads/" + filename);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        portfolio.setWorkimages(imagePaths);
        portfolioRepo.save(portfolio);
    }



    public List<Portfolio> getPublicPortfolios() {
        return portfolioRepo.findByIsPublicTrue();
    }

    public List<Portfolio> getPortfoliosByUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return portfolioRepo.findByOwner(user);
    }


    public List<Portfolio> getPublicPortfolios(String category) {
        if (category == null || category.isBlank()) {
            return portfolioRepo.findByIsPublicTrue();
        } else {
            return portfolioRepo.findByIsPublicTrueAndCategoryIgnoreCase(category);
        }
    }

    @Transactional
    public Portfolio updatePortfolioWithImages(
            Long id,
            Portfolio updatedData,
            MultipartFile[] newWorkImages,
            MultipartFile dpImage,
            List<String> workImagesToDelete,
            String ownerEmail
    ) {
        // 1. Fetch Portfolio and Authorization Check
        Portfolio portfolio = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (portfolio.getOwner() == null) {
            throw new AccessDeniedException("Portfolio owner is missing");
        }

        if (ownerEmail == null || ownerEmail.trim().isEmpty()) {
            throw new AccessDeniedException("Owner email is required");
        }

        String portfolioOwnerEmail = portfolio.getOwner().getEmail();
        if (!portfolioOwnerEmail.trim().equalsIgnoreCase(ownerEmail.trim())) {
            throw new AccessDeniedException("You are not the owner of this portfolio");
        }

        // 2. Update all non-image fields
        portfolio.setTitle(updatedData.getTitle());
        portfolio.setDescription(updatedData.getDescription());
        portfolio.setCategory(updatedData.getCategory());
        portfolio.setYear_of_exp(updatedData.getYear_of_exp());
        portfolio.setIsPublic(updatedData.getIsPublic());

        // 3. Handle Work Images Deletion
        List<String> currentWorkImages = portfolio.getWorkimages() != null
                ? new ArrayList<>(portfolio.getWorkimages())
                : new ArrayList<>();

        System.out.println("Current work images before deletion: " + currentWorkImages);
        System.out.println("Work images to delete: " + workImagesToDelete);

        if (workImagesToDelete != null && !workImagesToDelete.isEmpty()) {
            for (String imageUrl : workImagesToDelete) {
                System.out.println("Attempting to delete work image: " + imageUrl);
                try {
                    deleteFile(imageUrl);
                    System.out.println("File deleted from storage: " + imageUrl);
                } catch (IOException e) {
                    System.err.println("Failed to delete work image: " + imageUrl + ". Reason: " + e.getMessage());
                }

                boolean removed = currentWorkImages.remove(imageUrl);
                System.out.println("Removed from work images list: " + removed + " for " + imageUrl);
            }
        }

        System.out.println("Current work images after deletion: " + currentWorkImages);
        portfolio.setWorkimages(currentWorkImages);

        // 4. Handle New Work Images Upload
        if (newWorkImages != null && newWorkImages.length > 0) {
            for (MultipartFile file : newWorkImages) {
                if (!file.isEmpty()) {
                    try {
                        String newImageUrl = saveFile(file);
                        currentWorkImages.add(newImageUrl);
                        System.out.println("New work image added: " + newImageUrl);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("Failed to save new work image: " + file.getOriginalFilename());
                    }
                }
            }
        }

        // 5. Handle Display Picture (DP) Update
        if (dpImage != null && !dpImage.isEmpty()) {
            try {
                // Delete old DP if exists
                String oldDp = portfolio.getDp();
                if (oldDp != null && !oldDp.isEmpty()) {
                    try {
                        deleteFile(oldDp);
                        System.out.println("Old DP deleted: " + oldDp);
                    } catch (IOException e) {
                        System.err.println("Failed to delete old DP: " + oldDp);
                    }
                }

                // Save new DP
                String newDpUrl = saveFile(dpImage);
                portfolio.setDp(newDpUrl);
                System.out.println("New DP saved: " + newDpUrl);
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Failed to save new DP: " + dpImage.getOriginalFilename());
            }
        }

        // 6. Save portfolio to database
        Portfolio savedPortfolio = portfolioRepo.save(portfolio);
        System.out.println("Portfolio saved with work images: " + savedPortfolio.getWorkimages());
        return savedPortfolio;
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String uniqueFilename = UUID.randomUUID().toString() + "_" + originalFilename;

        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // ✅ Ensure relative path (always starts with /uploads/)
        String folderName = uploadPath.getFileName().toString(); // e.g. "uploads"
        return "/" + folderName + "/" + uniqueFilename;
    }


    /**
     * Delete file from storage
     */
    private void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // Remove leading slash if present
        String filePath = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
        Path path = Paths.get(filePath);

        if (Files.exists(path)) {
            Files.delete(path);
            System.out.println("File deleted successfully: " + filePath);
        } else {
            System.out.println("File not found: " + filePath);
        }
    }


    public Optional<Portfolio> getbyportfolioId(Long id) {
        return portfolioRepo.findById(id);
    }

    @Transactional
    public void deletePortfolio(Long id, String sellerEmail) {
        Portfolio portfolio = portfolioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Portfolio not found"));

        if (!portfolio.getOwner().getEmail().equals(sellerEmail)) {
            throw new AccessDeniedException("You are not the authorized person to delete");
        }

        contactRepository.deleteByPortfolioId(id);
        portfolioRepo.delete(portfolio);
    }
}
