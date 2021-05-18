package view;

import common.ValidationException;
import entity.Account;
import entity.Post;
import entity.RedditAccount;
import entity.Subreddit;
import entity.Comment;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.AccountLogic;
import logic.CommentLogic;
import logic.LogicFactory;
import logic.PostLogic;
import logic.RedditAccountLogic;
import logic.SubredditLogic;
import reddit.DeveloperAccount;
import reddit.wrapper.CommentSort;
import reddit.wrapper.PostWrapper;
import reddit.wrapper.RedditWrapper;
import reddit.wrapper.SubSort;
import reddit.wrapper.SubredditWrapper;

/**
 * @author Rodrigo Tavares & Wilker Fernandes de Sousa
 * @version 1.0 December 03, 2020
 *
 */
@WebServlet(name = "LoadDataView", urlPatterns = {"/LoadDataView"})
public class LoadDataView extends HttpServlet {

    private String message = null;
    private static final int MAX_POSTS = 2;
    private Subreddit subreddit = null;
    private RedditAccount redditAccount;
    private Post p;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>LoadDataView</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<div style=\"text-align: center;\">");
            out.println("<div style=\"display: inline-block; text-align: left;\">");

            out.println("<table style=\"margin-left: auto; margin-right: auto;\" >");
            out.println("<form action=\"LoadDataView\" method=\"post\">");

            out.println("<tr>");

            out.println("<td>");
            out.println("Subreddit Name ");
            out.println("</td>");

            out.println("<td>");
            out.println("<input type='text' name=\"nameInput\" value=\"\"><br>");
            out.println("</td>");

            out.println("</tr>");

            out.println("<tr>");

            out.println("<td>");
            out.println("Subreddit ");
            out.println("</td>");

            out.println("<td>");
            out.printf("<select name=\"%s\">", SubredditLogic.NAME);

            SubredditLogic subLogic = LogicFactory.getFor("Subreddit");
            out.printf("<option value=\"\" disabled selected>Select one</option>");
            subLogic.getAll().forEach(sub -> {
                out.printf("<option value=\"%s\">%s</option>", sub.getId(), sub.getName());
            });

            out.println("</select><br>");
            out.println("</td>");

            out.println("</tr>");

            out.println("<tr>");

            out.println("<td>");
            out.println("<input type=\"submit\" name=\"load\" value=\"Load\">");
            out.println("<td rowspan=2>");

            out.println("</tr>");

            out.println("</form>");
            out.println("</table>");

            if (message != null && !message.isEmpty()) {
                out.println("<p color=red>");
                out.println("<font color=red size=4px>");
                out.println(message);
                out.println("</font>");
                out.println("</p>");
            }

            out.println("</div>");
            out.println("</div>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        for (String k : m.keySet()) {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        }
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log("GET");
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        if (request.getParameter("load") != null) {

            //TODO fill in your reddit infromation here
            String clientID = "agVSwD1WixJT9Q";
            String clientSecret = "-Y1qbx4brQ9iuW_ySxmxkic6sUs";
            String redditUser = "adrianoreck";
            String algonquinUser = "reck0014";

            DeveloperAccount dev = new DeveloperAccount()
                    .setClientID(clientID)
                    .setClientSecret(clientSecret)
                    .setRedditUser(redditUser)
                    .setAlgonquinUser(algonquinUser);

            // 2.b. Creating logics
            SubredditLogic subLogic = LogicFactory.getFor("Subreddit");
            RedditAccountLogic raLogic = LogicFactory.getFor("RedditAccount");
            CommentLogic comLogic = LogicFactory.getFor("Comment");
            PostLogic postLogic = LogicFactory.getFor("Post");
            AccountLogic accLogic = LogicFactory.getFor("Account");

            //create a new scraper
            RedditWrapper scrap = new RedditWrapper();
            //authenticate and set up a page for wallpaper subreddit with 5 posts soreted by HOT order
            scrap.authenticate(dev).setLogger(false);

            subreddit = null;

            if (!request.getParameter("nameInput").equals("")) {

                scrap.configureCurentSubreddit(request.getParameter("nameInput"), 2, SubSort.BEST);
                SubredditWrapper sub = scrap.getCurrentSubreddit();

                Map<String, String[]> sampleMap = new HashMap<>();
                sampleMap.put(SubredditLogic.NAME, new String[]{sub.getName()});
                sampleMap.put(SubredditLogic.URL, new String[]{"https://www.reddit.com" + sub.getReletiveUrl()});
                sampleMap.put(SubredditLogic.SUBSCRIBERS, new String[]{Integer.toString(sub.getSubscribers())});

                subreddit = subLogic.createEntity(sampleMap);

                Subreddit foundSubreddit = subLogic.getSubredditWithUrl("https://www.reddit.com" + sub.getReletiveUrl());
                if (foundSubreddit == null) {
                    subLogic.add(subreddit);
                } else {
                    subreddit = foundSubreddit;
                }

            } else if (request.getParameter(SubredditLogic.NAME) != null) {

                String id = request.getParameter(SubredditLogic.NAME);
                subreddit = subLogic.getWithId(Integer.parseInt(id));

                scrap.configureCurentSubreddit(subreddit.getName(), 1, SubSort.BEST);

            } else {

                message = "Please enter the Subreddit name or select one from the drop-down.";
                processRequest(request, response);
                return;

            }

            //create a lambda that accepts post
            Consumer<PostWrapper> saveData = (PostWrapper post) -> {

                try {

                    if (post.isPinned()) {
                        return;
                    }

                    Map<String, String[]> sampleMap = new HashMap<>();
                    sampleMap.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(post.getAuthor().getCommentKarma())});
                    sampleMap.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(post.getAuthor().getLinkKarma())});
                    sampleMap.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(post.getAuthor().getCreated())});
                    sampleMap.put(RedditAccountLogic.NAME, new String[]{post.getAuthor().getName()});

                    redditAccount = raLogic.createEntity(sampleMap);
                    RedditAccount foundRedditAccount = raLogic.getRedditAccountWithName(post.getAuthor().getName());
                    if (foundRedditAccount == null) {
                        raLogic.add(redditAccount);
                    } else {
                        redditAccount = foundRedditAccount;
                    }

                    sampleMap = new HashMap<>();
                    sampleMap.put(PostLogic.CREATED, new String[]{postLogic.convertDateToString(post.getCreated())});
                    sampleMap.put(PostLogic.TITLE, new String[]{post.getTitle()});
                    sampleMap.put(PostLogic.COMMENT_COUNT, new String[]{Integer.toString(post.getCommentCount())});
                    sampleMap.put(PostLogic.POINTS, new String[]{Integer.toString(post.getVoteCount())});
                    sampleMap.put(PostLogic.UNIQUE_ID, new String[]{post.getUniqueID()});

                    p = postLogic.createEntity(sampleMap);

                    Post foundPost = postLogic.getPostWithUniqueId(post.getUniqueID());
                    if (foundPost == null) {
                        p.setRedditAccountId(redditAccount);
                        p.setSubredditId(subreddit);
                        postLogic.add(p);
                    } else {
                        p = foundPost;
                    }

                    System.out.println("************" + post.getTitle());
                    System.out.println("************" + post.getUrl());
//                post.configComments(1, post.getCommentCount(), CommentSort.CONFIDENCE);
                    post.configComments(2, 2, CommentSort.CONFIDENCE);
                    post.processComments(comment -> {
                        if (comment.isPinned() || comment.getDepth() == 0) {
                            return;
                        }

                        Map<String, String[]> sampleMap1 = new HashMap<>();
                        sampleMap1.put(RedditAccountLogic.COMMENT_POINTS, new String[]{Integer.toString(comment.getAuthor().getCommentKarma())});
                        sampleMap1.put(RedditAccountLogic.LINK_POINTS, new String[]{Integer.toString(comment.getAuthor().getLinkKarma())});
                        sampleMap1.put(RedditAccountLogic.CREATED, new String[]{raLogic.convertDateToString(comment.getAuthor().getCreated())});
                        sampleMap1.put(RedditAccountLogic.NAME, new String[]{comment.getAuthor().getName()});

                        RedditAccount commentRedditAccount = raLogic.createEntity(sampleMap1);
                        RedditAccount foundRedditAccount1 = raLogic.getRedditAccountWithName(comment.getAuthor().getName());
                        if (foundRedditAccount1 == null) {
                            raLogic.add(commentRedditAccount);
                        } else {
                            commentRedditAccount = foundRedditAccount1;
                        }

                        System.out.println((comment.isParrent() ? "----" : comment.getDepth() + ")") + "(" + comment.getAuthor().getName() + ")" + comment.getText());

                        sampleMap1 = new HashMap<>();
                        sampleMap1.put(CommentLogic.REPLYS, new String[]{Integer.toString(comment.getReplyCount())});
                        sampleMap1.put(CommentLogic.IS_REPLY, new String[]{Boolean.toString(!comment.isParrent())});
                        sampleMap1.put(CommentLogic.POINTS, new String[]{Integer.toString(comment.getVotes())});
                        sampleMap1.put(CommentLogic.CREATED, new String[]{comLogic.convertDateToString(comment.getCreated())});
                        sampleMap1.put(CommentLogic.TEXT, new String[]{comment.getText()});
                        sampleMap1.put(CommentLogic.UNIQUE_ID, new String[]{comment.getUniqueID()});

                        Comment c = comLogic.createEntity(sampleMap1);

                        Comment foundComment = comLogic.getCommentWithUniqueId(comment.getUniqueID());
                        if (foundComment == null) {
                            c.setRedditAccountId(commentRedditAccount);
                            c.setPostId(p);
                            comLogic.add(c);
                        }

                        message = "Data loaded successfully!";

                    });
                } catch (ValidationException ex) {
                    message = ex.getMessage();
                }
            };
            //get the next page and process every post
            scrap.requestNextPage().proccessCurrentPage(saveData);
            processRequest(request, response);

        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Loads data from Reddit";
    }

    private static final boolean DEBUG = true;

    public void log(String msg) {
        if (DEBUG) {
            String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
            getServletContext().log(message);
        }
    }

    public void log(String msg, Throwable t) {
        String message = String.format("[%s] %s", getClass().getSimpleName(), msg);
        getServletContext().log(message, t);
    }

}
