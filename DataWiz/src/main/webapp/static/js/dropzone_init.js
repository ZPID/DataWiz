var uploaderror = false;
// Start DROPZONE for project-material upload!
Dropzone.options.myDropzone = {
    uploadMultiple: true,
    autoProcessQueue: true,
    autoDiscover: false,
    parallelUploads: 1,
    // maxFiles : 20,
    maxFilesize: 2000, // MB
    dictMaxFilesExceeded: $('#maxFiles').val(),
    dictResponseError: $('#responseError').val(),
    dictDefaultMessage: $('#defaultMsg').val(),
    headers: {
        'X-CSRF-Token': $('input[name="_csrf"]').val()
    },
    init: function () {
        $("#dz-upload-button").prop('disabled', true);
        // upload button click event
        var myDropzone = this;
        $('#dz-upload-button').on("click", function (e) {
            uploaderror = false;
            myDropzone.processQueue();
        });
        // reset button click event
        $('#dz-reset-button').on("click", function (e) {
            myDropzone.removeAllFiles(true);
        });
        // adds the delete button after a file is added
        this.on("addedfile", function (file) {
            $("#dz-upload-button").prop('disabled', false);
            var removeButton = Dropzone
                .createElement("<button class='btn btn-block btn-danger btn-xs' style='margin-top: 5px;' >"
                    + $('#genDelete').val() + "</button>");
            // Capture the Dropzone instance as closure.
            var _this = this;
            removeButton.addEventListener("click", function (e) {
                e.preventDefault();
                e.stopPropagation();
                _this.removeFile(file);
            });
            // Add the button to the file preview element.
            file.previewElement.appendChild(removeButton);
        });
        this.on("removedfile", function (file) {
            $("#dz-upload-button").prop('disabled', false);
        });
        // shows a dialog after multiple upload finished
        this.on("queuecomplete", function (file, res) {
            console.log(uploaderror + ' - ' + file + ' - ' + res);
            // calls controller after successful multiple saving to reload the
            // page"
            if (!uploaderror) {
                setTimeout(function () {
                    $("form#my-dropzone").attr("enctype", "").attr("action", "multisaved").submit();
                }, 500);
            }
        });
        this.on("successmultiple", function (files, serverResponse) {
            myDropzone.processQueue();
        });
        var totalpercent = 0;
        this.on("totaluploadprogress", function (progress) {
            if (progress > totalpercent)
                totalpercent = progress;
            $('#loadstatebar .progress-bar').css('width', totalpercent + '%').attr('aria-valuenow', totalpercent).html(
                Math.round(totalpercent) + '%');
        });
        this.on("error", function (file, serverResponse) {
            uploaderror = true;
            if (serverResponse.indexOf("Exception") > 0) {
                console.log(serverResponse)
                this.defaultOptions.error(file, 'An error occurred!');
            }
            $("#loadstate").fadeOut("slow");
        });
        this.on("processingmultiple", function (files, serverResponse) {
            $("#loadstate").fadeIn("slow");
        });
        this.on("maxfilesexceeded", function (file) {
            $("#dz-upload-button").prop('disabled', true);
            this.removeFile(file);
        });
    }
};