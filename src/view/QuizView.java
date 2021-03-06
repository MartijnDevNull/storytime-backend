package view;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import controller.QuizController;

@Path("/quiz")
public class QuizView extends ViewSuper{
	private QuizController quizController = new QuizController();
	
	public QuizView() throws Exception {
		super();
	}
	
	@POST
	@Path("/add")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String addQuiz(@HeaderParam("token") String token, String input) throws Exception{
		return quizController.addQuiz(token, input);
	}
	
	@POST
	@Path("/add/child")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String addQuizToChild(String input){
		return quizController.addQuizToChild(input);
	}
	
	@DELETE
	@Path("/delete")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String deleteQuiz(String input){
		return quizController.deleteQuiz(input);
	}
	
	@GET
	@Path("/all")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllQuizes(){
		return quizController.getAllQuizes();
	}
	
	@GET
	@Path("/category/{id}")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllQuizesByCategory(@PathParam("id") int id){
		return quizController.getAllQuizesByCategory(id);
	}
	
	@GET
	@Path("/mentor/{id}")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String getAllQuizesByMentor(@PathParam("id") int id){
		return quizController.getAllQuizesByMentor(id);
	}
	
	@POST
	@Path("/update")
	@Consumes("application/json")
	@Produces(MediaType.APPLICATION_JSON)
	public String updateQuiz(String input){
		return quizController.updateQuiz(input);
	}
}