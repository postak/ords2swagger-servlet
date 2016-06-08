/**
 * Copyright (c) 2014, 2016, Oracle and/or its affiliates.
 * The Universal Permissive License (UPL), Version 1.0
 */
"use strict";

requirejs.config(
{
  // Path mappings for the logical module names
  paths:
  //injector:mainReleasePaths
  {
    "knockout": "libs/knockout/knockout-3.4.0.debug",
    "jquery": "libs/jquery/jquery-2.1.3",
    "jqueryui-amd": "libs/jquery/jqueryui-amd-1.11.4",
    "promise": "libs/es6-promise/promise-1.0.0",
    "hammerjs": "libs/hammer/hammer-2.0.4",
    "ojdnd": "libs/dnd-polyfill/dnd-polyfill-1.0.0",
    "ojs": "libs/oj/v2.0.1/debug",
    "ojL10n": "libs/oj/v2.0.1/ojL10n",
    "ojtranslations": "libs/oj/v2.0.1/resources",
    "knockout-amd-helpers": "libs/knockout/knockout-amd-helpers",
    "text": "libs/require/text",
    "signals": "libs/js-signals/signals"
  }
  //endinjector
  ,
  // Shim configurations for modules that do not expose AMD
  shim:
  {
    "jquery":
    {
      exports: ["jQuery", "$"]
    }
  }
}
);



/**
* A top-level require call executed by the Application.
* Although 'ojcore' and 'knockout' would be loaded in any case (they are specified as dependencies
* by the modules themselves), we are listing them explicitly to get the references to the 'oj' and 'ko'
* objects in the callback.
*
* For a listing of which JET component modules are required for each component, see the specific component
* demo pages in the JET cookbook.
*/
require(['ojs/ojcore', 'knockout', 'jquery', 'ojs/ojknockout', 'ojs/ojradioset',
         'ojs/ojinputtext', 'ojs/ojbutton', 'ojs/ojcollapsible', 'ojs/ojrouter'],
  // add additional JET components to the end of this list as needed
  // this callback gets executed when all required modules are loaded
  function(oj, ko, $)
  {
    function ViewModel() {
      var self = this;

      // Observable for user's name that will be specified via the ojInputText
      self.appTitle = "Oracle REST Data Service - Swagger file generator";
      self.filetype = ko.observable("ords");
      self.jdbcUrl = ko.observable("jdbc:oracle:thin:@localhost:1521/ccstop2.ittelecom88983.oraclecloud.internal");
      self.username = ko.observable("system");
      self.password = ko.observable("Password_01");
      self.rest_title = ko.observable("Enter document title");
      self.rest_description = ko.observable("Enter document description");
      self.rest_hostname = ko.observable("localhost");
      self.rest_port = ko.observable("8080");
      self.rest_basepath = ko.observable("/ords/telecomitalia");
      self.workspace = ko.observable("telecomitalia");


      self.setOrds = function (data, event) {
          self.filetype("ords");
          $("#workspace").hide();
      }

      self.setApex = function (data, event) {
          self.filetype("apex");
          $("#workspace").show();
      }

      self.swaggerOutput = ko.observable("");
      self.jsonTitle = ko.observable("Swagger File");
      self.downloadBtnLabel = ko.observable("Download");
      self.output = ko.observable("");

      self.downloadHandler = function () {

        var text = self.swaggerOutput();
        var textFileAsBlob = new Blob([text], {type: "text/plain;charset=utf-8"});

        // create a link for our script to 'click'
        var downloadLink = document.createElement("a");
        downloadLink.download = 'swagger.yaml';
        // provide text for the link. This will be hidden so you
        // can actually use anything you want.
        downloadLink.innerHTML = "My Hidden Link";
        // allow our code to work in webkit & Gecko based browsers
        // without the need for a if / else block.
        window.URL = window.URL || window.webkitURL;
        // Create the link Object.
        downloadLink.href = window.URL.createObjectURL(textFileAsBlob);
        // when link is clicked call a function to remove it from
        // the DOM in case user wants to save a second file.
        downloadLink.onclick = destroyClickedElement;
        // make sure the link is hidden.
        downloadLink.style.display = "none";
        // add the link to the DOM
        document.body.appendChild(downloadLink);
        // click the new link
        downloadLink.click();
      }

      function destroyClickedElement(event)
      {
        // remove the link from the DOM
        document.body.removeChild(event.target);
      }
      

      self.submitBt = function (data, event) {
        $("#lporesults").show();
        self.swaggerOutput ("loading....");

        document.body.style.cursor = 'progress';
        $.ajax({
            url: "/ords2swagger/getSwagger",
            data: {
                              filetype: self.filetype(),
                              jdbcUrl: self.jdbcUrl(),
                              username: self.username(),
                              password: self.password(),
                              rest_title: self.rest_title(),
                              rest_description: self.rest_description(),
                              rest_hostname: self.rest_hostname(),
                              rest_port: self.rest_port(),
                              rest_basepath: self.rest_basepath(),
                              workspace: self.workspace()
            },
            type: 'GET',
            success: function (data, textStatus, jqXHR) {
                self.swaggerOutput (data);
                
                document.body.style.cursor = 'auto';
            },
            error: function (data, textStatus, jqXHR) {
                self.swaggerOutput ('Error : ' + jqXHR);
                document.body.style.cursor = 'auto';
            },
      });

        document.body.style.cursor = 'auto';

          return true;
      }

    }

    // Create a view model and apply it to the document body. This causes any
    // ojComponents specified in the HTML data-bind to be initialized and their
    // attributes evaluated using the view model.
  //ko.applyBindings(new ViewModel(), document.body);


  $(document).ready(function() {
      ko.applyBindings(new ViewModel(), document.getElementById('postak'));
      });



  });
