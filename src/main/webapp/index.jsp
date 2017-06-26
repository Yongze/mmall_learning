<html>
<body>


SpringMVC upload file
<form name="formDemo" action="/manage/product/upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="SpringMVC Upload File"/>
</form>
Rich Text upload
<form name="formDemo" action="/manage/product/richtext_img_upload.do" method="post" enctype="multipart/form-data">
    <input type="file" name="upload_file"/>
    <input type="submit" value="Rich Text Upload File"/>
</form>
</body>
</html>
