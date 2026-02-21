package es.codeurjc.daw.library.service;
/*
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import es.codeurjc.daw.library.model.Equipo;
import es.codeurjc.daw.library.repository.ImageRepository;

@Service
public class PartidoService {

    @Autowired
    private ImageRepository imageRepository;

    public Equipo getImage(long id) {
        return imageRepository.findById(id).orElseThrow();
    }

    public Equipo createImage(InputStream inputStream) throws IOException {

        Equipo image = new Equipo();

        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);

        return image;
    }

    public Resource getImageFile(long id) throws SQLException {

        Equipo image = imageRepository.findById(id).orElseThrow();

        if (image.getImageFile() != null) {
            return new InputStreamResource(image.getImageFile().getBinaryStream());
        } else {
            throw new RuntimeException("Image file not found");
        }
    }

    public Equipo replaceImageFile(long id, InputStream inputStream) throws IOException {

        Equipo image = imageRepository.findById(id).orElseThrow();

        try {
            image.setImageFile(new SerialBlob(inputStream.readAllBytes()));
        } catch (Exception e) {
            throw new IOException("Failed to create image", e);
        }

        imageRepository.save(image);
        
        return image;
    }

    public Equipo deleteImage(long id) {
        Equipo image = imageRepository.findById(id).orElseThrow();
        imageRepository.deleteById(id);
        return image;
    }
}
    */