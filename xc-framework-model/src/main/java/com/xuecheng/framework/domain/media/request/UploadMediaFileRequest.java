package com.xuecheng.framework.domain.media.request;

import com.xuecheng.framework.model.request.RequestData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
public class UploadMediaFileRequest extends RequestData {
    String fileMd5;
    String fileName;
    Long fileSize;
    String fileType;
    String fileExt;
}
