package by.it.academy.enterprise.controller;

import by.it.academy.enterprise.fileupload.MyUploadForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

@Controller
public class FileUploadController {
    private final Logger logger = LoggerFactory.getLogger("LOG");

    @InitBinder
    public void initBinder(WebDataBinder dataBinder) {
        Object target = dataBinder.getTarget();
        if (target == null) {
            logger.trace("InitBinder target = null");
            return;
        }
        if (target.getClass() == MyUploadForm.class) {
            dataBinder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
            logger.trace("InitBinder set form MyUploadForm from target and registerCustomEditor ");
        }
    }

    @RequestMapping(value = "/uploadOneFile", method = RequestMethod.GET)
    public String uploadOneFileHandler(Model model) {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        logger.info("Request /uploadOneFile ${myUploadForm} parameter set success");
        return "uploadOneFile";
    }

    @RequestMapping(value = "/uploadOneFile", method = RequestMethod.POST)
    public String uploadOneFileHandlerPOST(HttpServletRequest request, Model model,
                                           @ModelAttribute("myUploadForm") MyUploadForm myUploadForm) {
        return this.doUpload(request, model, myUploadForm);

    }

    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.GET)
    public String uploadMultiFileHandler(Model model) {
        MyUploadForm myUploadForm = new MyUploadForm();
        model.addAttribute("myUploadForm", myUploadForm);
        logger.info("Request /uploadMultiFile ${myUploadForm} parameter set success");
        return "uploadMultiFile";
    }

    @RequestMapping(value = "/uploadMultiFile", method = RequestMethod.POST)
    public String uploadMultiFileHandlerPOST(HttpServletRequest request, Model model,
                                             @ModelAttribute("myUploadForm") MyUploadForm myUploadForm) {
        return this.doUpload(request, model, myUploadForm);
    }

    private String doUpload(HttpServletRequest request, Model model, MyUploadForm myUploadForm) {
        String description = myUploadForm.getDescription();
        String uploadRootPath = request.getServletContext().getRealPath("upload");
        File uploadRootDir = new File(uploadRootPath);
        if (!uploadRootDir.exists()) {
            uploadRootDir.mkdirs();
            logger.trace("File catalog created by path " + uploadRootPath);
        }
        CommonsMultipartFile[] fileDatas = myUploadForm.getFileDatas();
        List<File> uploadedFiles = new ArrayList();
        for (CommonsMultipartFile fileData : fileDatas) {
            String name = fileData.getOriginalFilename();
            if (name != null && name.length() > 0) {
                try {
                    File serverFile = new File(uploadRootDir.getAbsolutePath() + File.separator + name);
                    BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
                    stream.write(fileData.getBytes());
                    stream.close();
                    uploadedFiles.add(serverFile);
                    logger.trace("Uploaded file: " + serverFile.getAbsolutePath());
                } catch (Exception e) {
                    logger.error("doUpload error {}", e);
                }
            }
        }
        model.addAttribute("description", description);
        logger.info("Request ${description} parameter set success");
        model.addAttribute("uploadedFiles", uploadedFiles);
        logger.info("Request ${uploadedFiles} parameter set success");
        return "uploadResult";
    }

}
