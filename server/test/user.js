process.env.NODE_ENV = 'test'

var chai        = require("chai")
var chaiHttp    = require("chai-http")
var should      = chai.should()
var mocha       = require("mocha")

chai.use(chaiHttp)

var utils = require("../utils")

var server = require("../config/apiInfo.json")
var host = server.endpoint + ":" + server.port

/**
 * HTTP PUT
 * @param facebook_id
 * @param push_key
 * @param name
 */
describe("Facebook Login", function(){
    it("Facebook login with proper data", function(done){
        let path = "/login"
        let data = {
            "facebook_id"   : "some string",
            "push_key"      : "some string",
            "name"          : "name"
        }    
            
        chai.request(host)
            .post(path)
            .set("content-type", "application/x-www-form-urlencoded")
            .send(data)
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.should.have.property("data")
                res.body.code.should.equal("00")            
                done()
            })
    })    
})

/**
 * HTTP PUT
 * @param facebook_id
 * @param lat
 * @param lon
 */
describe("Update current location on significant change(GPS)", function(){
    it("Update with proper data", function(done){
        let path = "/updateLocation"
        let data = {
            "facebook_id"   : "1234",
            "lat"           : "43.659599",
            "lon"           : "-79.397449"
        }

        chai.request(host)
            .put(path)
            .set("content-type", "application/x-www-form-urlencoded")
            .send(data)
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")            
                done()
            })
    })
})

 /**
 * HTTP GET
 * @param facebook_id
 * @param building_name
 */
describe("Send list of users, except the requester with facebook_id, that are in the building with building_name", function(){
    it("Send list with proper data", function(done){
        let facebook_id  = "1111"
        let building_name  = "ba"

        let path = "/getUserInBuilding"

        chai.request(host)
            .get(path)
            .query({facebook_id: facebook_id, building_name: building_name})
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")
                res.body.data[0].facebook_id.should.not.equal(facebook_id)
                res.body.data[0].current_location.should.equal(building_name)
                done()
            })
    })
})

 /**
 * HTTP GET
 * @param lat
 * @param lon
 */
describe("Send list of buildings given GPS info (lat, lon)", function(){
    it("Send list with proper data", function(done){
        let lat  = "43.659555"
        let lon  = "-79.397696"

        let path = "/getBuildings"

        chai.request(host)
            .get(path)
            .query({lat: lat, lon: lon})
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")
                res.body.data[0].should.have.property("building_id")
                res.body.data[0].should.have.property("building_name")
                res.body.data[0].should.have.property("building_address")
                res.body.data[0].should.have.property("building_polygon")
                done()
            })
    })
})

/**
 * HTTP PUT
 * @param facebook_id
 * @param gender
 */
describe("Update user's gender (Male or Female) given facebook_id", function(){
    it("Update with proper data", function(done){
        let path = "/updateGender"
        let data = {
            "facebook_id"   : "1234",
            "gender"        : "Male"
        }

        chai.request(host)
            .put(path)
            .set("content-type", "application/x-www-form-urlencoded")
            .send(data)
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")            
                done()
            })
    })
})

/**
 * HTTP PUT
 * @param facebook_id
 * @param gender
 */
describe("Update user's gender preference (Male or Female) given facebook_id", function(){
    it("Update with proper data", function(done){
        let path = "/updatePreference"
        let data = {
            "facebook_id"   : "1234",
            "gender"        : "Female"
        }

        chai.request(host)
            .put(path)
            .set("content-type", "application/x-www-form-urlencoded")
            .send(data)
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")            
                done()
            })
    })
})

/**
 * HTTP PUT
 * @param facebook_id
 * @param gender
 * @param preference
 */
describe("Update both user's gender and preference (Male or Female) given facebook_id", function(){
    it("Update with proper data", function(done){
        let path = "/updateGenderPreference"
        let data = {
            "facebook_id"   : "1234",
            "gender"        : "Male",
            "preference"    : "Female"
        }

        chai.request(host)
            .put(path)
            .set("content-type", "application/x-www-form-urlencoded")
            .send(data)
            .end(function(err, res){
                res.should.have.status(200)
                res.should.be.json
                res.body.should.be.a("object")
                res.body.should.have.property("code")
                res.body.code.should.equal("00")            
                done()
            })
    })
})
