package repo;

import org.apache.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Andrey Smirnov
 * @date 13.08.2018
 */
public class CheckerMainServlet extends HttpServlet {
    public static final Logger logger = Logger.getLogger(CheckerMainServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String oldRevision = request.getParameter("oldRevision");
        String newRevision = request.getParameter("newRevision");
        String branchName = request.getParameter("branchName");
        if (oldRevision != null && newRevision != null && branchName != null) {
            try {
                GitApi.prepareRepos(branchName, oldRevision, newRevision);
                Compare comparer = new Compare();
                comparer.doCompare();
                String diffs = comparer.getDiffs();
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text/html");
                request.setAttribute("resultText", diffs);
                comparer.clearDiffs();
                response.getWriter().print(request.getAttribute("resultText").toString());
                logger.info("Finishing compare branch = " + branchName + " newRevision = " + newRevision + " oldRevision = " + oldRevision
                        + "\r\nDiffs : " + diffs);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }


}
