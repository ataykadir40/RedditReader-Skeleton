package view;

import entity.Host;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import logic.HostLogic;
import logic.LogicFactory;

/**
 *
 * @author lutfi
 */
@WebServlet(name = "HostTableViewJSP", urlPatterns = {"/HostTableViewJSP"})
public class HostTableViewJSP extends HttpServlet{
    private void fillTableData(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getServletPath();
        req.setAttribute("entities", extractTableData(req));
        req.setAttribute("request", toStringMap(req.getParameterMap()));
        req.setAttribute("path", path);
        req.setAttribute("title", path.substring(1));
        req.getRequestDispatcher("/jsp/ShowTable-Account.jsp").forward(req, resp);
    }

    @SuppressWarnings("empty-statement")
    private List<?> extractTableData(HttpServletRequest req) {
        //get the seraching parameters for columns
        //Create the logic
        //set attribute to label the search column
        String search1 = req.getParameter("searchText1");
        String search2 = req.getParameter("searchText2");
        HostLogic logic = LogicFactory.getFor("Host");
        req.setAttribute("columnName", logic.getColumnNames());
        req.setAttribute("columnCode", logic.getColumnCodes());
        req.setAttribute("SearchCol1", "Search by Host name");    
        req.setAttribute("SearchCol2", "Search by Extraction type");           
        List<Host> list;
        //if first search text box is empty or null check second one. If both of them empty then get all records.        
        if ((search1 != null && !search1.isEmpty()) || (search2!=null && !search2.isEmpty())) {
            if (search1 != null && !search1.isEmpty()) {
                list= new ArrayList();
                list.add(logic.getHostWithName(search1));
            }
            else list = logic.getHostWithExtractionType(search2);
        } else {
            list = logic.getAll();
        }
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        return appendDatatoNewList(list, logic::extractDataAsList);
    }

    private <T> List<?> appendDatatoNewList(List<T> list, Function<T, List<?>> toArray) {
        List<List<?>> newlist = new ArrayList<>(list.size());
        list.forEach(i -> newlist.add(toArray.apply(i)));
        return newlist;
    }

    private String toStringMap(Map<String, String[]> m) {
        StringBuilder builder = new StringBuilder();
        m.keySet().forEach((k) -> {
            builder.append("Key=").append(k)
                    .append(", ")
                    .append("Value/s=").append(Arrays.toString(m.get(k)))
                    .append(System.lineSeparator());
        });
        return builder.toString();
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param req  servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("POST");
        HostLogic logic;
        Map<String, String[]> map = req.getParameterMap();
        if (map.containsKey("deleteMark")) {
            logic = LogicFactory.getFor("Host");
            for (String delete : map.get("deleteMark")) {
                logic.delete(logic.getWithId(Integer.valueOf(delete)));
            }
        }
        if (map.containsKey("edit")) {
            logic = LogicFactory.getFor("Host");
//            if (map.get("edit").equals("Update")) {
                logic.update(logic.updateEntity(map));
//            }
        }        
        fillTableData(req, resp);
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param req  servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("GET");
        doPost(req, resp);
    }

    /**
     * Handles the HTTP <code>PUT</code> method.
     *
     * @param req  servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("PUT");
        doPost(req, resp);
    }

    /**
     * Handles the HTTP <code>DELETE</code> method.
     *
     * @param req  servlet request
     * @param resp servlet response
     *
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        log("DELETE");
        doPost(req, resp);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Host Table using JSP";
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