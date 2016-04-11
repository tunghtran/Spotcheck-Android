function init() 
{
  var ROOT = 'https://' + window.location.host + '/_ah/api';
  gapi.client.load('spotcheck', 'v1', loadCallback, ROOT);
}

function loadGapi() 
{
  // Set the API key
  gapi.client.setApiKey('207366745467-g6m1dg5ub0m6u4v4gfksi6mbqtg5ptob.apps.googleusercontent.com');
  // Set: name of service, version and callback function
  gapi.client.load('spotcheck', 'v1', loadCallback);
}

function loadCallback()
{

}

function getAccounts() 
{
  var restURL =
    //"https://xxx.appspot.com/_ah/api/yyy/v1/article";
   'https://' + window.location.host + '/_ah/api/spotcheck/v1/getAccount';

  var xhttp;
  if (window.XMLHttpRequest) 
  { // code for good browsers
      xhttp = new XMLHttpRequest();
  } else 
  {   // code for IE6, IE5
      xhttp = new ActiveXObject("Microsoft.XMLHTTP");
  }
  xhttp.onreadystatechange = function() 
  {
      if (xhttp.readyState == 4 && xhttp.status == 200) 
      {
        var result = xhttp.responseText;
        alert(result);
      }
  }
  xhttp.open("GET", restURL, true);
  xhttp.send();

  // Get the list of previous scores
gapi.client.spotcheck.getAccount().execute(function(resp) {
  alert("resp");
});

}

function submit()
{
  var f = $("#first").val();
  var l = $("#last").val();
  var e = $("#email").val();
  var p = $("#password").val();

  alert("f = " + f + ", l = " + l + ", e = " + e);
  
  // Insert an account
  gapi.client.spotcheck.saveAccount({'email': e}).execute(function(resp) {
    alert(resp);
  });

  // Insert a new account
  //gapi.client.spotcheck.saveAccount({'firstName': f}).execute(function(resp) {
  //  console.log(resp);
  //});
}