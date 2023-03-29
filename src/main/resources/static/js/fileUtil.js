const fileUtil = {
  change: function(obj, previewId) {
    const targetObj = $(obj);
    const file = $(obj)[0].files[0];
    const fileSize = (file.size / 1024) / 1024;
    const fileName = file.name;
    const fileExt = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length);

    if ($("#globalFileExt").val().indexOf(fileExt) < 0) {
        alert(msg.file.not.ext);
        targetObj.val("");
        return false;
    }

    if (fileSize > $("#globalFileSize").val()) {
      alert(msg.file.not.size);
      targetObj.val("");
      return false;
    }

    // Preview image
    if (previewId != "") {
      let preview = $("#" + previewId);
      let reader = new FileReader();

      reader.addEventListener("load",function () {
        preview.attr("src", reader.result);
        preview.show();
      }, false);

      if (file) {
        reader.readAsDataURL(file);
      }
    }
  }
}