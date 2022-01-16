package by.it.academy.enterprise.controller;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class FIleLoadController {
    private final Logger logger = LoggerFactory.getLogger("LOG");

    @RequestMapping(value = "/loadOneFile/{file_name}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getOneFile(@PathVariable("file_name") String fileName,
                                                          HttpServletRequest request, Model model) {
        try {
            String uploadRootPath = request.getServletContext().getRealPath("upload");
            InputStreamResource resource = new InputStreamResource(
                    new FileInputStream(uploadRootPath+"\\"+fileName));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                    .contentLength(fileName.length())
                    .body(resource);
        } catch (IOException ex) {
            logger.error("LoadFile error: {}", ex);
            throw new RuntimeException("IOError writing file to output stream");
        }
    }

    @RequestMapping(value = "/fileRead/{file_name}")
    public void getFile(
            @PathVariable("file_name") String fileName,
            HttpServletResponse response, HttpServletRequest request) {
        try {
            String uploadRootPath = request.getServletContext().getRealPath("upload");
            InputStream is = new FileInputStream(new File(uploadRootPath+"\\"+fileName));
            org.apache.commons.io.IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
            logger.trace("Request /fileRead ${file_name} open success");
        } catch (IOException ex) {
            logger.error("GetFile error: {}", ex);
            throw new RuntimeException("IOError writing file to output stream");
        }

    }

    @RequestMapping(value = "/getFileUploadPath")
    public String getLoadFile(Model model, HttpServletRequest request) {
        String uploadRootPath = request.getServletContext().getRealPath("upload");
        model.addAttribute("file_path", uploadRootPath);
        logger.trace("Request /getFileUploadPath ${file_path} parameter set success");
        return "loadFilePath";
    }

}
