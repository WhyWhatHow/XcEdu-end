package com.xuecheng.framework.domain.media;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @Author: mrt.
 * @Description:
 * @Date:Created in 2018/1/24 10:04.
 * @Modified By:
 */
@Data
@ToString
@NoArgsConstructor
public class MediaFileProcess_m3u8 extends MediaFileProcess {

    //ts列表
    private List<String> tsList;

   public  MediaFileProcess_m3u8( List<String> list){
       tsList= list ;
   }
}
