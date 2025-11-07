### 各种注解
@RequestPart("file") : @RequestPart("file") 处理multipart/form-data类型请求，核心作用是从多部份的请求中提取指定名称的部分，然后绑定到方法参数上

### 函数

// 添加 “字段 LIKE % 参数 %” 的模糊匹配条件
queryWrapper.like(条件, 数据库字段, 参数);

// 添加 “字段 = 参数” 的精确匹配条件
queryWrapper.eq条件, 数据库字段, 参数);

##### multipartFile 的常见方法
这些方法属于 Spring 框架中的MultipartFile接口，用于处理 HTTP 文件上传时的文件信息，以下是每个方法的具体作用：
1. getName()
   作用：获取表单中文件字段的参数名（即 HTML 表单中`<input type="file" name="xxx">`里的name属性值）。
   示例：若前端表单是`<input name="avatar" type="file">`，则getName()返回"avatar"。
2. getOriginalFilename()
   作用：获取上传文件的原始文件名（包含后缀，如"user.jpg"）。
   注意：不同浏览器 / 客户端可能返回不同格式（如带路径或纯文件名），需自行处理兼容性。
3. getContentType()
   作用：获取文件的MIME 类型（如图片文件返回"image/jpeg"，文本文件返回"text/plain"）。
   用途：用于判断文件类型（如校验是否为图片、视频等）。
4. getBytes()
   作用：将上传的文件内容以 ** 字节数组（byte[]）** 的形式返回。
   用途：可用于文件内容的读取、存储（如存入数据库 BLOB 字段）或进一步处理（如图片压缩、格式转换）。
5. getInputStream()
   作用：获取文件内容的输入流（InputStream）。
   用途：流式读取文件内容（适合大文件，避免一次性加载到内存导致 OOM），常用于文件拷贝、解析（如读取 Excel、CSV 内容）。
6. getResource()
   作用：将文件封装为 Spring 的Resource资源对象，便于统一资源操作（如资源加载、URL 获取）。
7. getSize()
   作用：获取文件的大小（字节数）。
   用途：用于文件大小校验（如限制上传文件不能超过 10MB）。
   这些方法覆盖了文件上传场景中 “识别文件元信息（名称、类型、大小）” 和 “操作文件内容（读取、存储）” 的核心需求，是 Spring 处理文件上传的关键 API。
