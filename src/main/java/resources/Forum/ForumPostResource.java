package resources.Forum;
//Datentypen
import java.util.List;

//Quarkus zeug
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONObject;
import org.json.JSONException;
//HTTP Requests
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.POST;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;


//Logging
import java.util.logging.Logger;
import java.util.logging.Level;

//Eigene Imports
import model.Forum.ForumPost;
import orm.Forum.ForumPostOrm;

//Sicherheits Zeug
import javax.ws.rs.core.SecurityContext;

import com.google.errorprone.annotations.IncompatibleModifiers;

import org.eclipse.microprofile.jwt.JsonWebToken;
import java.security.Principal;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

//Logging
import java.util.logging.Logger;
@Path("/forum/post")
// @RequestScoped
@ApplicationScoped
public class ForumPostResource {
    private static final Logger log = Logger.getLogger(ForumPostResource.class.getName());

    @Inject
    ForumPostOrm forumPostOrm;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public List<ForumPost> getPosts( @QueryParam("post") Long postId,
                            @QueryParam("title") String title,  
                            @QueryParam("user")Long userId,
                            @QueryParam("topic")Long topicId,
                            @QueryParam("editor")Long editorId)
    {
        log.info("ForumResource/getTopics");
        if(postId != null){
            log.info("ForumResource/getTopics/id");
            return forumPostOrm.getPostsById(postId);
        }
        else if(topicId != null){
            log.info("ForumResource/getTopics/name");
            return forumPostOrm.getPostsByTopic(topicId);
        }
        else if(userId != null){
            log.info("ForumResource/getPosts/user");
            return forumPostOrm.getPostsByUser(userId);
        }
        else if(editorId != null){
            log.info("ForumResource/getPosts/editor");
            return forumPostOrm.getPostsByEditor(editorId);
        }
        else if(title != null){
            log.info("ForumResource/getPosts/category");
            return forumPostOrm.getPostByTitel(title);
        }
        else{
                log.info("ForumResource/getPosts/all");
            return forumPostOrm.getAllPosts();
        }

    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String addPost(String postString,@QueryParam("topic")Long topicId, @QueryParam("user")Long userId){
        log.info("ForumPostResource/addPost");
        JSONObject myJson;
        try {
            myJson = new JSONObject(postString);
        }catch(JSONException err){
            log.info("Error" + err.toString());
            return "Error wehen converting";
        }
        ForumPost forumPost = new ForumPost();

        forumPost.setTitle(myJson.getString("title"));
        try {
            forumPost.setContent(myJson.getJSONObject("content").toString());
        } catch (Exception e) {
            log.log(Level.SEVERE, "Result{0}", e.getMessage());
            return "This should never Happen";
        }
        /**
         * TODO:
         * -    Check permissions
         * -    Save Pictures
         */
        return forumPostOrm.addPost(forumPost, topicId, userId);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String updatePost(ForumPost forumPost, @QueryParam("user") Long userId){
        log.info("ForumPostResource/updatePost");
        /**
         * TODO:
         * -    Check permissions
         */
        return forumPostOrm.updatePost(forumPost, userId);
    }
    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public String deletePost(ForumPost forumPost, @QueryParam("user") Long userId){
        log.info("ForumPostResource/deletePost");
        /**
         * TODO:
         * -    Check permissions
         */
        return forumPostOrm.deletePost(forumPost, userId);
    }
}
