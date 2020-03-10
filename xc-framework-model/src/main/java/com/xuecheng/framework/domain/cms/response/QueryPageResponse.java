package com.xuecheng.framework.domain.cms.response;

import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class QueryPageResponse extends ResponseResult {

    public QueryPageResponse(ResultCode resultCode , QueryResult queryResult) {
        super(resultCode);
        this.queryResult = queryResult ;
    }
    QueryResult queryResult ;

}
