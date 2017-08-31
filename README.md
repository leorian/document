<div style="text-align:center"><h1>文档转换中心接口调用说明文档</h1></div>
<hr/>
<h3>1、接口调用部分</h3>
<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(1)、单文档异步转换请求</h4>
<div style="margin-top:8px;">
<strong>简要概述：</strong>目前该接口支持的源文件格式为<span style="color:blue;"><strong>(doc、docx、ppt、pptx、xls、xlsx)</strong></span>,转换后的目标文件格式<span style="color:blue"><strong>(pdf)</strong></span>
</div>
<div style="margin-top:8px;">
<strong>请求地址：</strong><span style="color:blue;">http://document.317hu.com/document/docOperation/doc2pdfAsync</span>
</div>
<div style="margin-top:8px;">
<strong>请求方式：</strong><span style="color:blue;">HTTP POST</span>
</div>
<div style="margin-top:8px;">
<strong>请求参数：</strong>
<br/>
<span style="margin-left:75px;color:blue">docFileUrl&nbsp;<span style="color:red">*</span>：</span><span>待转换文件在线链接地址（如上传七牛之后的返回的文档URL链接地址）</span>
<br/>
<span style="margin-left:75px;color:blue">callBackUrl：</span><span>异步转换成功之后的回调地址及通知地址（<i>非必填项</i>）</span>
</div>
<div style="margin-top:8px;">
<strong>返回结果：</strong>
<br/>
<span>
<pre>
  {
    "data": {
        "createTimeStamp": "1493858622500",
        "reAction": false,
        "taskContent": "http://okxyat5ou.bkt.clouddn.com/Java%E6%B3%9B%E5%9E%8B%E4%B8%8E%E5%8F%8D%E5%B0%84%E5%9C%A8%E5%BC%80%E5%8F%91%E4%B8%AD%E5%BA%94%E7%94%A8.pptx",
        "taskId": "93f9d616-5729-4889-b49c-27a9ff63ec52",
        "taskStatus": "WAITING"
    },
    "success": true
}
</pre>
<br/>
<span style="color:blue">taskId:</span><span>此次转换请求的任务执行情况跟踪的任务ID</span>
<br/>
<span style="color:blue">taskContent:</span><span>待转换文件在线链接地址，等同docFileUrl</span>
<br/>
<span style="color:blue">taskStatus:</span><span>任务状态（<span style="color:#5bc0de">WATING：等待</span>，<span style="color:#f0ad4e">EXECUTING:正在执行</span>，<span style="color:#5cb85c">SUCCESS:转换成功</span>，<span style="color:#d9534f">FAILURE:转换失败</span>）</span>
</div>

<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(2)、单文档同步转换请求</h4>
<div style="margin-top:8px;">
<strong>简要概述：</strong>目前该接口支持的源文件格式为<span style="color:blue;"><strong>(doc、docx、ppt、pptx、xls、xlsx)</strong></span>,转换后的目标文件格式<span style="color:blue"><strong>(pdf)</strong></span>
</div>
<div style="margin-top:8px;">
<strong>请求地址：</strong><span style="color:blue;">http://document.317hu.com/document/docOperation/doc2pdfSync</span>
</div>
<div style="margin-top:8px;">
<strong>请求方式：</strong><span style="color:blue;">HTTP POST</span>
</div>
<div style="margin-top:8px;">
<strong>请求参数：</strong>
<br/>
<span style="margin-left:75px;color:blue">docFileUrl&nbsp;<span style="color:red">*</span>：</span><span>待转换文件在线链接地址（如上传七牛之后的返回的文档URL链接地址）</span>
</div>
<div style="margin-top:8px;">
<strong>返回结果：</strong>
<br/>
<span>
<pre>
{
    "data": {
        "createTimeStamp": "1493859562226",
        "executeTimeStamp": "1493859562228",
        "finishTimeStamp": "1493859585106",
        "reAction": false,
        "taskContent": "http://okxyat5ou.bkt.clouddn.com/Java%E6%B3%9B%E5%9E%8B%E4%B8%8E%E5%8F%8D%E5%B0%84%E5%9C%A8%E5%BC%80%E5%8F%91%E4%B8%AD%E5%BA%94%E7%94%A8.pptx",
        "taskContentExt": "pptx",
        "taskContentLength": "1.80MB",
        "taskId": "d28ce8f0-166d-4709-9b08-ea7a66de856e",
        "taskResult": "http://okxyat5ou.bkt.clouddn.com/FoyuZDhmtkxk91ikaoNBIXoyydno",
        "taskResultExt": "pdf",
        "taskResultLength": "1.84MB",
        "taskStatus": "SUCCESS"
    },
    "success": true
}
</pre>
<br/>
<span style="color:blue">createTimeStamp:</span><span>文档转换请求创建时间时间戳</span>
<br/>
<span style="color:blue">executeTimeStamp:</span><span>文档转换请求工作线程执行开始时间时间戳</span>
<br/>
<span style="color:blue">finishTimeStamp:</span><span>文档转换请求执行结束时间时间戳</span>
<br/>
<span style="color:blue">reAction:</span><span>任务执行超时时，是否重新执行过标识位（文档监控页面管理员使用）</span>
<br/>
<span style="color:blue">taskContent:</span><span>待转换文件在线链接地址，等同docFileUrl</span>
<br/>
<span style="color:blue">taskContentExt:</span><span>待转换文件文档格式（doc/docx/ppt/pptx/xls/xlsx）</span>
<br/>
<span style="color:blue">taskContentLength:</span><span>待转换文件文档大小</span>
<br/>
<span style="color:blue">taskId:</span><span>此次转换请求的任务执行情况跟踪的任务ID</span>
<br/>
<span style="color:blue">taskResult:</span><span>转换后目标文件在线链接地址</span>
<br/>
<span style="color:blue">taskResultExt:</span><span>转换后目标文件文档格式（pdf）</span>
<br/>
<span style="color:blue">taskResultLength:</span><span>转换后目标文件文档大小</span>
<br/>
<span style="color:blue">taskStatus:</span><span>任务状态（<span style="color:#5bc0de">WATING：等待</span>，<span style="color:#f0ad4e">EXECUTING:正在执行</span>，<span style="color:#5cb85c">SUCCESS:转换成功</span>，<span style="color:#d9534f">FAILURE:转换失败</span>）</span>
<br/>
<span style="color:red">errorCode:</span><span style="color:red">错误编码：如（E10001）</span>
<br/>
<span style="color:red">errorMessage:</span><span style="color:red">错误信息：如（Parameter Not Defined）</span>
</div>

<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(3)、多文档异步转换请求</h4>
<div style="margin-top:8px;">
<strong>简要概述：</strong>目前该接口支持的源文件格式为<span style="color:blue;"><strong>(doc、docx、ppt、pptx、xls、xlsx)</strong></span>,转换后的目标文件格式<span style="color:blue"><strong>(pdf)</strong></span>
</div>
<div style="margin-top:8px;">
<strong>请求地址：</strong><span style="color:blue;">http://document.317hu.com/document/docOperation/doc2pdfBatchAsync</span>
</div>
<div style="margin-top:8px;">
<strong>请求方式：</strong><span style="color:blue;">HTTP POST</span>
</div>
<div style="margin-top:8px;">
<strong>请求参数：</strong>
<br/>
<span style="margin-left:75px;color:blue">docFileUrlList&nbsp;<span style="color:red">*</span>：</span><span>待转换文件在线链接地址,用英文逗号分隔（如上传七牛之后的返回的文档URL链接地址）</span>
<br/>
<span style="margin-left:75px;color:blue">callBackUrl：</span><span>异步转换成功之后的回调地址及通知地址（<i>非必填项</i>）</span>
</div>
<div style="margin-top:8px;">
<strong>返回结果：</strong>
<br/>
<span>
<pre>
{
    "data": [
        {
            "createTimeStamp": "1493860704943",
            "reAction": false,
            "taskContent": "http://okxyat5ou.bkt.clouddn.com/1.ppt",
            "taskId": "a1e5a65f-5b89-42fb-9ca8-8625774890df",
            "taskStatus": "WAITING"
        },
        {
            "createTimeStamp": "1493860704946",
            "reAction": false,
            "taskContent": "http://okxyat5ou.bkt.clouddn.com/2.docx",
            "taskId": "8cc27f76-f3cd-45b2-b3aa-497e572d5ebe",
            "taskStatus": "WAITING"
        },
        {
            "createTimeStamp": "1493860704951",
            "reAction": false,
            "taskContent": "http://okxyat5ou.bkt.clouddn.com/3.doc",
            "taskId": "d6fad0a6-14a1-40a1-b568-1eb2aa0db2e7",
            "taskStatus": "WAITING"
        },
        {
            "createTimeStamp": "1493860704956",
            "reAction": false,
            "taskContent": "http://okxyat5ou.bkt.clouddn.com/Java%E6%B3%9B%E5%9E%8B%E4%B8%8E%E5%8F%8D%E5%B0%84%E5%9C%A8%E5%BC%80%E5%8F%91%E4%B8%AD%E5%BA%94%E7%94%A8.pptx",
            "taskId": "29d5a4a7-c7e5-44ff-9450-bfacb0a8256a",
            "taskStatus": "WAITING"
        }
    ],
    "success": true
}
</pre>
<br/>
<span style="color:blue">taskId:</span><span>此次转换请求的任务执行情况跟踪的任务ID</span>
<br/>
<span style="color:blue">taskContent:</span><span>待转换文件在线链接地址，等同docFileUrl</span>
<br/>
<span style="color:blue">taskStatus:</span><span>任务状态（<span style="color:#5bc0de">WATING：等待</span>，<span style="color:#f0ad4e">EXECUTING:正在执行</span>，<span style="color:#5cb85c">SUCCESS:转换成功</span>，<span style="color:#d9534f">FAILURE:转换失败</span>）</span>
</div>

<h4>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(4)、文档转换请求任务状态跟踪查询接口(<span style="color:red">返回结果跟callbackUrl回调结果一样</span>)</h4>
<div style="margin-top:8px;">
<strong>简要概述：</strong>目前该接口支持的源文件格式为<span style="color:blue;"><strong>(doc、docx、ppt、pptx、xls、xlsx)</strong></span>,转换后的目标文件格式<span style="color:blue"><strong>(pdf)</strong></span>
</div>
<div style="margin-top:8px;">
<strong>请求地址：</strong><span style="color:blue;">http://document.317hu.com/document/docOperation/doc2pdfTask</span>
</div>
<div style="margin-top:8px;">
<strong>请求方式：</strong><span style="color:blue;">HTTP POST</span>
</div>
<div style="margin-top:8px;">
<strong>请求参数：</strong>
<br/>
<span style="margin-left:75px;color:blue">taskId：</span><span>任务ID</span>
<br/>
<span style="margin-left:105px;">或者</span>
<br/>
<span style="margin-left:75px;color:blue">docFileUrl：</span><span>待转换文件在线链接地址（如上传七牛之后的返回的文档URL链接地址）</span>
</div>
<div style="margin-top:8px;">
<strong>返回结果：</strong>
<br/>
<span>
<pre>
{
    "data": {
        "createTimeStamp": "1493859562226",
        "executeTimeStamp": "1493859562228",
        "finishTimeStamp": "1493859585106",
        "reAction": false,
        "taskContent": "http://okxyat5ou.bkt.clouddn.com/Java%E6%B3%9B%E5%9E%8B%E4%B8%8E%E5%8F%8D%E5%B0%84%E5%9C%A8%E5%BC%80%E5%8F%91%E4%B8%AD%E5%BA%94%E7%94%A8.pptx",
        "taskContentExt": "pptx",
        "taskContentLength": "1.80MB",
        "taskId": "d28ce8f0-166d-4709-9b08-ea7a66de856e",
        "taskResult": "http://okxyat5ou.bkt.clouddn.com/FoyuZDhmtkxk91ikaoNBIXoyydno",
        "taskResultExt": "pdf",
        "taskResultLength": "1.84MB",
        "taskStatus": "SUCCESS"
    },
    "success": true
}
</pre>
<br/>
<span style="color:blue">createTimeStamp:</span><span>文档转换请求创建时间时间戳</span>
<br/>
<span style="color:blue">executeTimeStamp:</span><span>文档转换请求工作线程执行开始时间时间戳</span>
<br/>
<span style="color:blue">finishTimeStamp:</span><span>文档转换请求执行结束时间时间戳</span>
<br/>
<span style="color:blue">reAction:</span><span>任务执行超时时，是否重新执行过标识位（文档监控页面管理员使用）</span>
<br/>
<span style="color:blue">taskContent:</span><span>待转换文件在线链接地址，等同docFileUrl</span>
<br/>
<span style="color:blue">taskContentExt:</span><span>待转换文件文档格式（doc/docx/ppt/pptx/xls/xlsx）</span>
<br/>
<span style="color:blue">taskContentLength:</span><span>待转换文件文档大小</span>
<br/>
<span style="color:blue">taskId:</span><span>此次转换请求的任务执行情况跟踪的任务ID</span>
<br/>
<span style="color:blue">taskResult:</span><span>转换后目标文件在线链接地址</span>
<br/>
<span style="color:blue">taskResultExt:</span><span>转换后目标文件文档格式（pdf）</span>
<br/>
<span style="color:blue">taskResultLength:</span><span>转换后目标文件文档大小</span>
<br/>
<span style="color:blue">taskStatus:</span><span>任务状态（<span style="color:#5bc0de">WATING：等待</span>，<span style="color:#f0ad4e">EXECUTING:正在执行</span>，<span style="color:#5cb85c">SUCCESS:转换成功</span>，<span style="color:#d9534f">FAILURE:转换失败</span>）</span>
<br/>
<span style="color:red">errorCode:</span><span style="color:red">错误编码：如（E10001）</span>
<br/>
<span style="color:red">errorMessage:</span><span style="color:red">错误信息：如（Parameter Not Defined）</span>
</div>
<h3>2、页面监控部分</h3>
<h4>(1)、图表展示异步文档转换请求执行情况（3s定时刷新一次）</h4>
![](http://i.imgur.com/hPDhj6c.png)
<img src="http://i.imgur.com/hPDhj6c.png"/>
<h4>(2)、表格展示异步文档转换请求执行情况（3s定时刷新一次）</h4>
![](http://i.imgur.com/nLqv4FZ.png)
<img src="http://i.imgur.com/nLqv4FZ.png"/>
<h4>(3)、手动控制文档转换请求</h4>
WAITING:正在等待执行的任务队列，文档转换中心应用重启可以继续执行。
<br/>
EXECUTING:正在执行中的任务队列，文档转换中心应用重启不可以继续执行，需要管理员手动重新执行。
![](http://i.imgur.com/Mjlu15E.png)
<img src="http://i.imgur.com/Mjlu15E.png"/>
<h3>3、文档转换请求模拟测试部分</h3>
异步文档转换请求模拟
![](http://i.imgur.com/1IIP02o.png)
<img src="http://i.imgur.com/1IIP02o.png"/>
同步文档转换请求模拟
![](http://i.imgur.com/02P2u95.png)
<img src="http://i.imgur.com/02P2u95.png"/>
异步文档转换请求模拟（批量）
![](http://i.imgur.com/GS1lKyO.png)
<img src="http://i.imgur.com/GS1lKyO.png"/>
<h3>4、错误码对应关系</h3>
<div style="color:red">
<pre>
    E10001("E10001", "Parameter Not Defined", "参数未定义！"),
    E10002("E10002", "Database Operation Error", "数据库操作异常！"),
    E10003("E10003", "WorkerThread Execute Error", "工作线程执行异常！"),
    E10004("E10004", "File Convert Error", "文件转换异常！"),
    E10005("E10005", "Not Find Relation Data", "未查询到相关数据！"),
    E10006("E10006", "File Unreachable", "文件链接地址不可达！"),
    E10007("E10007", "File Format UnSupport", "文件格式不支持！"),
    E10008("E10008", "File Size UnKnown", "文件大小未知"),
    E10009("E10009", "File Content Unreachable", "文件内容获取不到！"),
    E10010("E10010", "File Convert Error", "文件转换失败！"),
    E10011("E10011", "QiNiu Upload Error", "七牛上传失败"),
    E10012("E10012", "File Not Exist", "文件不存在！"),
    E10013("E10013", "Pdf Format Not Need Convert", "pdf格式文件无需转换！"),
    E10014("E10014", "Count Search Error", "统计查询异常！"),
    E10015("E10015", "QiNiu Get File Info Error", "七牛获取文件信息异常！"),
    E10016("E10016", "Net Work Error", "网络连接异常！"),
    E10017("E10017", "Open Office Service Error", "文件转换服务异常！"),
    E10018("E10018", "Login Failure Public Private Key Expire", "用户密码公钥私钥加密策略过期，请重新进入登录页面！"),;
</pre>
</div>
