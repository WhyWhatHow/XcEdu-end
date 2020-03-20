package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.exception.RuntimeExceptionCast;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;


@Service
public class PagePreviewService {
    @Autowired
    CmsPageService cmsPageService;
    @Autowired
    CmsConfigService cmsConfigService;


    /**
     * TODO PS : 该项目缺少模板管理模块(不可以进行模板的增删改查),所以,默认模板已添加到mongoDB中
     * 页面静态化流程:（由字符串生成）
     * 模板+ 数据 = 静态化文件
     * 1. cmsPage 中 dataUrl: 页面静态化所需数据的来源url
     * 2. 模拟请求,从dataUrl中获取数据模型
     * 3. 根据模板id 获取 模板信息, 然后根据templateFileId 获取模板文件Id
     * 4. 根据templateFileId 获取到模板文件 =》 字符串
     * 5. 页面静态化
     **/
    public String getPreviewPageByPageId(String id) {
        // 获取页面信息
        CmsPageResult res = cmsPageService.findByPageId(id);
        if (res.isSuccess()) {
            CmsPage cmsPage = res.getCmsPage();
            //1 获取数据模型
            Map map = getModelByCmsPage(cmsPage);
            //2 获取模板信息
            String template = getTemplateByCmsPage(cmsPage);
            // 3 静态化
            String html = gengerateHtml(template, map);
            return html;
        }
        return null;
    }

    @Autowired
    GridFsTemplate gridFsTemplate;
    @Autowired
    CmsTemplateRepository repository;
    @Autowired
    GridFSBucket gridFSBucket;

    private String getTemplateByCmsPage(CmsPage cmsPage) {
        String templateId = cmsPage.getTemplateId();
        if (StringUtils.isEmpty(templateId)) {
            RuntimeExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        Optional<CmsTemplate> opt = repository.findById(templateId);
        if (opt.isPresent()) {
            CmsTemplate template = opt.get();
            String templateFileId = template.getTemplateFileId();
            if (StringUtils.isNotEmpty(templateFileId)) {
                GridFSFile file = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
                if(file == null){
                    RuntimeExceptionCast.cast(CmsCode.CMS_GRIDFS_READ_FILE_FAIL);
                }
                GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(file.getObjectId());
                GridFsResource gridFsResource = new GridFsResource(file, gridFSDownloadStream);
                //从流中取数据
                try {
                    String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                    return content;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
//

        return null;
    }

    @Autowired
    RestTemplate restTemplate;

    // 根据dataUrl获取数据模型
    private Map getModelByCmsPage(CmsPage cmsPage) {
        String dataUrl = cmsPage.getDataUrl();
        // 处理dataUrl is null
        if (StringUtils.isEmpty(dataUrl)) {
            RuntimeExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }
        ResponseEntity<Map> entity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = entity.getBody();
        return body;
    }



    /**
     *  生成静态页面
     * @param templateContent 模板内容
     * @param map   数据模型
     * @return 静态化文件 html的字符串模式
     */
     private String gengerateHtml(String templateContent, Map map)   {

        //创建配置对象
        Configuration configuration = new Configuration(Configuration.getVersion());
        //创建模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template",templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用api进行静态化
            String content = FreeMarkerTemplateUtils.processTemplateIntoString(template, map);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
