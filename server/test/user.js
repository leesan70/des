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
            "lat"           : "-79.397449",
            "lon"           : "43.659599"
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
