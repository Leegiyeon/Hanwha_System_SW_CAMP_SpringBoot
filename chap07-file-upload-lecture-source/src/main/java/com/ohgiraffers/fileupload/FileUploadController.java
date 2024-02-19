package com.ohgiraffers.fileupload;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Controller
public class FileUploadController {

    /* 설명. build 된 파일 업로드 할 경로를 가져오기 위해 ResourceLoader 의존 성 주입 받기 */
    @Autowired
    private ResourceLoader resourceLoader;

    @PostMapping("single-file")
    public String singleFileUpload(@RequestParam MultipartFile singleFile,
                                   @RequestParam String singleFileDescription,
                                   RedirectAttributes rttr) throws IOException {
        /* 설명. enctype = "multipart/form-data" */
        System.out.println("singleFile = " + singleFile);
        System.out.println("singleFileDescription = " + singleFileDescription);

        /* 설명. build 경로의 static에 있는 파일 업로드 할 곳의 경로를 받아온다. */
        Resource resource = resourceLoader.getResource("classpath:static/uploadFiles/img/single");
//        System.out.println("빌드된 single 디렉토리 = " + resource.getFile().getAbsolutePath());

        String filePath = resource.getFile().getAbsolutePath();

        /* 설명. 사용자가 넘긴 파일을 확인하고 rename 해 보자*/
        String originalName = singleFile.getOriginalFilename();
        System.out.println("originalName = " + originalName);

        String ext = originalName.substring(originalName.lastIndexOf("."));
        System.out.println("ext = " + ext);

        String savedName = UUID.randomUUID().toString().replace("-", "") + ext;
        System.out.println("savedName = " + savedName);


        /* 설명. 우리가 지정한 경로로 파일 저장 후 */
        try {

            singleFile.transferTo(new File(filePath + "/" + savedName));

            /* 설명. DB 다녀오는 BL 구문 작성(DB에 저장)
            *   BL이 성공하면 화면의 재료를 Redirect*/

            rttr.addFlashAttribute("message", "파일업로드 성공");
            rttr.addFlashAttribute("img", "uploadFiles/img/single/" + savedName);
            rttr.addFlashAttribute("singleFileDescription", singleFileDescription);
        } catch (IOException e){

            /* 설명. try 구문에서 예외가 발생하면 파일만 올라가면 안되므로 다시 지워줌 */
            new File(filePath + "/" + savedName).delete();
        }

        return "redirect:/result";
    }

    @GetMapping("result")
    public void result() {}

}