package  com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsPageParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest {

    @Autowired
    CmsPageRepository cmsPageRepository;

    @Test
   public  void findAll(){
        int page = 0 ;
        int size = 10 ;
        Pageable pageable = PageRequest.of(page,size);
        Page<CmsPage> all = cmsPageRepository.findAll(pageable);
        System.out.println(all);
    }
    @Test
    public void testFindByIndex(){
        String siteId = "5a751fab6abb5044e0d19ea1";
        String pageName = "首页";
        String pageWebPath= "/index.html";
        CmsPage cmspage = cmsPageRepository.findBySiteIdAndPageNameAndPageWebPath(siteId, pageName, pageWebPath);
        System.out.println(cmspage);
    }
    //添加
    @Test
    public void testInsert(){
    //定义实体类
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId("s01");
        cmsPage.setTemplateId("t01");
        cmsPage.setPageName("测试页面");
        cmsPage.setPageCreateTime(new Date());
        List<CmsPageParam> cmsPageParams = new ArrayList<>();
        CmsPageParam cmsPageParam = new CmsPageParam();
        cmsPageParam.setPageParamName("param1");
        cmsPageParam.setPageParamValue("value1");
        cmsPageParams.add(cmsPageParam);
        cmsPage.setPageParams(cmsPageParams);
        cmsPageRepository.save(cmsPage);
        System.out.println(cmsPage);
    }
    @Test
    public void testDelete(){
        cmsPageRepository.deleteById("5e591b56948436237891b6f3");

    }
    @Test
    public void testUpdate(){
        Optional<CmsPage> cmsPage = cmsPageRepository.findById("5e5f51559484362dd81ae49d");
        if (cmsPage.isPresent()) {
            CmsPage page = cmsPage.get();
            page.setPageName("娃哈哈");
            cmsPageRepository.save(page);
        }
    }
}
