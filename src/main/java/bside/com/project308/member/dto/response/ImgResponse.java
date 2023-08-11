package bside.com.project308.member.dto.response;

import bside.com.project308.member.dto.ImgDto;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@NoArgsConstructor
public class ImgResponse {
    public static List<ImgDto> imgs = new ArrayList<>();

    {
        for (int i = 1; i < 19; i++) {
            ImgDto imgDto = new ImgDto("img" + i, "https://project-308.kro.kr/images/" + i + ".png");
            imgs.add(imgDto);
        }
    }


}
