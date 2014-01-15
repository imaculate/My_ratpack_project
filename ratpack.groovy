@GrabResolver("https://oss.jfrog.org/artifactory/repo")
@Grab("io.ratpack:ratpack-groovy:0.9.0-SNAPSHOT")
import static ratpack.groovy.Groovy.*
import static ratpack.form.Forms.form
import java.util.*;
/**
 * Created by imaculate on 12/31/13.
 */


ratpack {
    handlers {
def userEmail;
/** this will hold the email address of the logged user
 *
 */
def userName;

def projects;

        def noOfProjects;

/** this will hold the firstName of the user */

get() {
    render groovyTemplate ( [errormessage:""], "LoginForm.html")
}


post("addproject"){
	def form = parse form()
    def name  = form.name
    def grailsVersion = form.grailsVersion
    def gitRepo = form.gitRepo
    def destination = form.destination

    Project.addProject( userEmail, name , grailsVersion, gitRepo, destination)
	
   projects = Project.getProjects(userEmail)

    noOfProjects = projects.size();


    render groovyTemplate( [message: "You have successfully added a new project",username: firstName, projectCollection: projects, nop: noOfProjects],"HomePage.html")

}





post("login") {


    def form = parse form()	
    def usermail = form.email
    def password = form.password
    if (Users.validateUser(usermail, password)) {
        userEmail = usermail

        firstName = Users.getName(userEmail)
        projects = Project.getProjects(userEmail)

       noOfProjects = projects.size();
	def map = [username: firstName, projectCollection: projects, nop: noOfProjects, message:"", errormessage:""] as java.util.Map;



        render groovyTemplate(map,"HomePage.html" )


    } else {
        render groovyTemplate( [errormessage: "Wrong Username or Password"], "LoginForm.html")
    }

}

post("changePassword"){
	def form = parse form()
    def oldPassword = form.oldpassword
    def newPassword = form.p2

    if(Users.validateUser(userEmail,oldPassword)){
        Users.changePassword(userEmail,newPassword)
       render groovyTemplate ( [message: "Your password has been successfully updated", errormessage:""], "ChangePassword.html")
    }else{
       render groovyTemplate( [errormessage: "Please enter correct password", message:""],"ChangePassword.html")
    }

}

get("ChangePassword.html"){
    render groovyTemplate([errormessage: "", message: ""] , "ChangePassword.html")
}

get("AddProject.html"){
    render groovyTemplate("AddProject.html")
}


post("deleteproject"){
	def form = parse form()
    def id =  form.id
    Project.deleteProject( id)
    projects = Project.getProjects(userEmail)

    noOfProjects = projects.size() ;


    render groovyTemplate([message: "You have successfully deleted this project",username: firstName,
                projectCollection: projects, nop: noOfProjects, errormessage:""], "HomePage.html")

}

post("editproject"){
def form = parse form()
    def id = form.id
    def name  = form.name
    def grailsVersion = form.grailsVersion
    def gitRepo = form.gitRepo
    def destination = form.destination
    
    Project.updateProject( id, name , grailsVersion, gitRepo, destination)
	projects = Project.getProjects(userEmail)

    noOfProjects = projects.size();


   render groovyTemplate( [message: "You have successfully updated this project",username: firstName,
                projectCollection: projects, nop: noOfProjects, errormessage:""], "HomePage.html" )

}

get("EditProject.html"){
	
   	def id = request.queryParams.id
    def name  = request.queryParams.name
    def version = request.queryParams.version
    def git = request.queryParams.gitRepo
    def dest = request.queryParams.dest

    render groovyTemplate([ pid:id, name: name,grailsVersion: version, gitRepo: git , destination:dest], "EditProject.html")

}
post("deploy"){
	def form = parse form()
    def git = form.git;
    def dest = form.dest;
	def version = form.version;
	def folder = git.substring(git.lastIndexOf("/")+1, git.lastIndexOf("."))

   
        if(Project.clone(git)){
        render groovyTemplate( [message: "You have successfully cloned this project from $git",username: firstName, errormessage:"", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
	
                
    }else{
	render groovyTemplate( [message: "",username: firstName, errormessage:"An error occured when cloning, check your address", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
	}
	 if(Project.build(git)){
        render groovyTemplate( [message: "You have successfully built this project",username: firstName, errormessage:"", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
	
                
    }else{
	render groovyTemplate( [message: "",username: firstName, errormessage:"An error occured while building, check your application content", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
}
        if(Project.deploy(folder, destination)){
        render groovyTemplate( [message: "You have successfully deployed this project to $dest",username: firstName, errormessage:"", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
	
                
    }else{
render groovyTemplate( [message: "",username: firstName, errormessage:"An error occured on deploying, check that you have added autodeployer's ssh key to $dest", projectcollection: projects, nop: noOfProjects], "HomePage.html") 
}
	//you will come catch catch all possible errors , invalid address and ssh keys. 

}
post("logout"){
    userName = null;
    userEmail = null;
    projects = null;

   noOfProjects = null;

    render groovyTemplate([errormessage:""],"LoginForm.html")

}
        assets "public"
    }
}



