package it.uniroma3.siw.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Picture;
import it.uniroma3.siw.repository.PictureRepository;
import jakarta.transaction.Transactional;

import static it.uniroma3.siw.model.Picture.PATH;

@Service
public class PictureService {
    
	@Autowired
    private PictureRepository pictureRepository;
	
    @Value("${upload.dir}")
    private String uploadDir;

    public Picture getImage(Long id) {
        return pictureRepository.findById(id).orElse(null);
    }

    @Transactional
	public void savePhysicalImage(byte[] data, String name) throws IOException {
		
        Path uploadPath = (Path) Paths.get(uploadDir + PATH);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        Path filePath = uploadPath.resolve(name);
        Files.write(filePath, data);
	}

	public void deletePhysicalImage(Picture photo) {
		File file = new File(uploadDir + photo.getNome());
		file.delete();
	}
	
	public boolean existsImage(Long imgId) {
		return this.pictureRepository.existsById(imgId);
	}
}