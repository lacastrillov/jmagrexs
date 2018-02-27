/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function FileUploader() {

    var Instance = this;
    
    Instance.SLICE_SIZE = 5 * 1024 * 1024; // 5MB

    Instance.init = function () {
        
    };
    
    this.startFileUpload= function(multifilefieldId, endpoint, progressFunc){
        Instance.endpoint= endpoint;
        Instance.progressFunc= progressFunc;
        Instance.currentFile= 0;
        Instance.files= document.getElementById(multifilefieldId).files;
        if(Instance.files.length>0){
            Instance.file= Instance.files[0];
            Instance.uploadFile();
        }
    };
    
    this.uploadFile= function() {
        Instance.uploaded = 0;
        Instance.currentSlice = 0;
        Instance.fileSize = Instance.file.size;
        Instance.totalSlices= Math.floor(Instance.fileSize / Instance.SLICE_SIZE);

        if (Instance.fileSize % Instance.SLICE_SIZE !== 0) {
            Instance.totalSlices += 1;
        }
        //document.getElementById('slice_progress').innerHTML = "# Slices " + Instance.totalSlices;
        Instance.uploadSlice(Instance.currentSlice);
    };

    this.uploadSlice= function(i) {
        var fd = new FormData();

        if (i + 1 < Instance.totalSlices) {
            fd.append("file", Instance.file.slice(i * Instance.SLICE_SIZE, (i + 1) * Instance.SLICE_SIZE));
        } else {
            fd.append("file", Instance.file.slice(i * Instance.SLICE_SIZE));
        }
        fd.append("fileName", Instance.file.name);
        fd.append("fileType", Instance.file.type);
        fd.append("fileSize", Instance.file.size);
        fd.append("slice", i);

        var xhr = new XMLHttpRequest();
        xhr.upload.addEventListener("progress", Instance.uploadProgress, false);
        xhr.addEventListener("load", Instance.uploadComplete, false);
        xhr.addEventListener("error", Instance.uploadFailed, false);
        xhr.addEventListener("abort", Instance.uploadCanceled, false);
        xhr.open("POST", Instance.endpoint, true);
        xhr.send(fd);
    };

    this.uploadProgress= function(evt) {
        var percentComplete = Math.round((Instance.uploaded + evt.loaded) * 100 / Instance.fileSize);
        percentComplete = Math.min(percentComplete, 100);
        Instance.progressFunc(Instance.file.name, percentComplete, false);
    };

    this.uploadComplete= function(evt) {
        if (Instance.currentSlice < Instance.totalSlices - 1) {
            Instance.uploaded += Instance.SLICE_SIZE;
        } else {
            Instance.uploaded += Instance.fileSize % Instance.totalSlices;
        }
        Instance.currentSlice++;
        if (Instance.currentSlice < Instance.totalSlices) {
            Instance.uploadSlice(Instance.currentSlice);
        }else{
            Instance.currentFile++;
            if(Instance.currentFile<Instance.files.length){
                Instance.file= Instance.files[Instance.currentFile];
                Instance.uploadFile();
            }else{
                Instance.progressFunc(Instance.file.name, 100, true);
            }
        }
    };

    this.uploadFailed= function(evt) {
        console.log("There was an error attempting to upload the file.");
    };

    this.uploadCanceled= function(evt) {
        console.log("The upload has been canceled by the user or the browser dropped the connection.");
    };

    Instance.init();
}