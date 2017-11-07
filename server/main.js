var express = require("express")
var app 	= express()

// HTTP
var http = require("http")

// File System Module
var fs = require("fs")

// Number of CPU for fork
var numCPUs = require("os").cpus().length

// Clustering Module
var cluster = require("cluster")

// Parameter Parser
var bodyParser 	= require("body-parser")
var multer 		= require("multer")

var path = require("path")

function listenPort (port){
	var server = http.createServer(app).listen(port, function(){
		console.log("Server is running on port : " + port)	
	})
}

function initializeServer(app){
	var port = 5000
    listenPort(port)
    
    app.use( bodyParser.json() )       // to support JSON-encoded bodies
    app.use(bodyParser.urlencoded({     // to support URL-encoded bodies
      extended: true
    }))
    
	// Static Files
    app.use(express.static(path.join(__dirname, "public")))

    // Dependent Files
	var user 		= require("./user")

    app.post("/login", user.login)
	app.put("/updateLocation", user.updateLocation)
	app.get("/getUserInBuilding", user.getUserInBuilding)
	app.get("/getBuildings", user.getBuildings)
	app.put("/updateGender", user.updateGender)
	app.put("/updatePreference", user.updatePreference)
}

// Server clustering
if (cluster.isMaster){
	for (var i = 0; i < numCPUs; i++){
		cluster.fork()
	} 
	
	Object.keys(cluster.workers).forEach(function(id) {
	   console.log("Running with PID : " + cluster.workers[id].process.pid);
	})
	
	cluster.on('exit', function(worker, code, signal) {
		console.log("worker " + worker.process.pid + " died");
	})
} else {
	initializeServer(app)
}