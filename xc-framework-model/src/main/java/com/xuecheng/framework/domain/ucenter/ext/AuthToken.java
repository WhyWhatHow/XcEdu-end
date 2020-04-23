package com.xuecheng.framework.domain.ucenter.ext;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@ToString
@NoArgsConstructor
public class AuthToken {
    String access_token; //访问token
    String refresh_token; //刷新token
    String user_token; //jti令牌
    public AuthToken(String access_token,String refresh_token,String user_token){
        this.access_token=access_token;
        this.refresh_token=refresh_token;
        this.user_token=user_token;
    }
}

//{
//    "access_token":
//      "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4NzU4MjY5NiwianRpIjoiYTc5ZDMyZjgtNGY1My00OWRiLTk4NDItNzVkMjM1ZWFkMzAzIiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.PnYpKCMpNFcILY4XaWWa6j7BYzn-Vx6Nt7GhpPqvQoP0Nc97hf96hRDZBSMFUgVXSaHRSoAkPa8tjh8uBeYQQHHaaWe_QjaPiFcmzV7Yj2g0LZUtlvdsRov6XqgQsaX9Lfw2fVjcnylYNWe4o1r1DGcGw7n2h-4YrNwUy-IXwMuicImLbhB21DTgQIEUPenIHM3HEKHuhfRaYtz9gs5hxe3oaYyIe0yRAfBkSi6bXauVIBPlgjZO_xyJKzFumgegHHjrSjRHZJUUGftM8BQrjcaLd1QSpdhSdsG8D5GJNDlLmBh6sAAKR1oVXRKyIp2TAxFhyKcEDLw2_wur7tNxUQ",
//    "token_type":
//      "bearer",
//    "refresh_token":
//          "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJjb21wYW55SWQiOm51bGwsInVzZXJwaWMiOm51bGwsInVzZXJfbmFtZSI6Iml0Y2FzdCIsInNjb3BlIjpbImFwcCJdLCJhdGkiOiJhNzlkMzJmOC00ZjUzLTQ5ZGItOTg0Mi03NWQyMzVlYWQzMDMiLCJuYW1lIjpudWxsLCJ1dHlwZSI6bnVsbCwiaWQiOm51bGwsImV4cCI6MTU4NzU4MjY5NiwianRpIjoiNDQyNjAxYmEtNGVmOC00NjBlLTg3YTYtY2Q3ZDU0NDdhZjA2IiwiY2xpZW50X2lkIjoiWGNXZWJBcHAifQ.Co-Ej9EkTMxqJoBXPCo7yi4S_-nmHyjQSiOZhQIXTOF2MrEDs4ju4heRRGuDON4wHAckr9Zhi5wEQ2fjLbCeWf3FTAUFEuLB4dk_JutBA7Uwkh6G2qYA5cDFjjfBA2GFUl25lpoKON2XzR9Nlo5X4AsQJIK0Jfr7rjEs5_lODEWVAbJ9SpsNr55JlslSqB7Cqpq7ZvjzZ3k_c6A05Er_Rj-N901zagEB_8ozn43D5QXycCoFbYgmL_wTGwaLsb423guajZ9w964W6HfbXHq9qVdI7Rugb7OofQ1yDuOgOKl0DTKTFmVWcujMTVqN9pGdNdZLq9XU-XwWC9abRMtCrg",
//    "expires_in":
//              43199,
//    "scope":
//             "app",
//    "jti":
//          "a79d32f8-4f53-49db-9842-75d235ead303"
//}