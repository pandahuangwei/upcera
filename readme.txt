配置说明：
1、配置config.properties，目前深圳配置是正确的,沈阳的配置是复制深圳的，当前是不生效的:
##深圳
sz_req_url=http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService
sz_client_code=AECKQJS
sz_check_word=fCfDsgQatwYbF8dW9slS2WfRdqGbNAKO

##沈阳
sy_req_url=http://bsp-oisp.sf-express.com/bsp-oisp/sfexpressService
sy_client_code=AECKQJSR
sy_check_word=fCfDsgQatwYbF8dW9slS2WfRdqGbNAKOG
深圳的配置正确，只能获取到深圳的运单信息。
沈阳的运单信息的话，需要用沈阳的月结账号在丰桥上新开一个账号重新申请客户编码(client_code)
和验证码(check_word),当申请得沈阳的账号的时候，将配置信息配置到上面的sy_client_code、sy_check_word
之后，则沈阳的运单信息也可以获取。

当上面的配置正确的时候，可以按如下步骤进行操作，获得批量运单信息->
操作步骤：
1、填入需要获取运单信息的运单号深圳的填入sz-waybill.xls中，沈阳的填入sy-waybill.xls中
    (sz-waybill,sy-waybill这两个名字不能改变);
2、双击start.bat启动.(如果有权限限制，请右键该文件 -- 以管理员身份运行)；
3、可以从2中打开的小黑窗口中看到提示信息；
4、运单信息获取结束，会在当前目录生成Excel文档(格式:SZ-年月日时分秒.xls)。



