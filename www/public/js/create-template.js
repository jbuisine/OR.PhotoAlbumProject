Dropzone.options.fileTemplate = {
    url: "/template-save-photo",
    method: "POST",
    maxFilesize: 5,
    paramName: "photo",
    acceptedFiles: "image/jpg",
    addRemoveLinks: true,
    previewTemplate: document.getElementById('template-preview').innerHTML,
    sending: function(file, xhr, formData){
        alert("send");
        formData.append('templateName',  $("#templateName").val());
    }
};
