package murach.cart;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import murach.business.Cart;
import murach.business.LineItem;
import murach.business.Product;
import murach.data.ProductIO;

public class CartServlet extends HttpServlet {
  
  /**
   * OpenShift with Tomcat does not allow reading a file from under Webapps
   * folder but rather only under the OPENSHIFT_DATA_DIR location specified by
   * this environment variable. Since this location is dynamic for the
   * application, we write out the datafile the first time the application is
   * run. From that point on, a check is made for file existence and if it
   * exists then there is no reason to write again.
   *
   * @throws ServletException
   */
  public void initIfNeeded() throws ServletException {
    //
    String path = this.getActualFile();
    if (!isFile(path)) {
      try {
        //add two Users
        ProductIO.addRecord(new Product("8601",
          "86 (the band) - True Life Songs and Pictures",
          14.95), path);
        ProductIO.addRecord(new Product("pf01",
          "Paddlefoot - The first CD",
          12.95), path);
        ProductIO.addRecord(new Product("pf02",
          "Paddlefoot - The second CD",
          14.95), path);
        ProductIO.addRecord(new Product("jr01",
          "Joe Rut - Genuine Wood Grained Finish",
          14.95), path);

      } catch (IOException ex) {
        Logger.getLogger(CartServlet.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
  

    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        
        ServletContext sc = getServletContext();
        
        initIfNeeded();
        
        // get current action
        String action = request.getParameter("action");
        if (action == null) {
            action = "cart";  // default action
        }

        // perform action and set URL to appropriate page
        String url = "/index.jsp";
        if (action.equals("shop")) {            
            url = "/index.jsp";    // the "index" page
        } 
        else if (action.equals("cart")) {
            String productCode = request.getParameter("productCode");
            String quantityString = request.getParameter("quantity");

            HttpSession session = request.getSession();
            Cart cart = (Cart) session.getAttribute("cart");
            if (cart == null) {
                cart = new Cart();
            }

            //if the user enters a negative or invalid quantity,
            //the quantity is automatically reset to 1.
            int quantity;
            try {
                quantity = Integer.parseInt(quantityString);
                if (quantity < 0) {
                    quantity = 1;
                }
            } catch (NumberFormatException nfe) {
                quantity = 1;
            }

            String path = this.getActualFile();
            Product product = ProductIO.getProduct(productCode, path);

            LineItem lineItem = new LineItem();
            lineItem.setProduct(product);
            lineItem.setQuantity(quantity);
            if (quantity > 0) {
                cart.addItem(lineItem);
            } else if (quantity == 0) {
                cart.removeItem(lineItem);
            }

            session.setAttribute("cart", cart);
            url = "/cart.jsp";
        }
        else if (action.equals("checkout")) {
            url = "/checkout.jsp";
        }

        sc.getRequestDispatcher(url)
                .forward(request, response);
    }
    
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        doPost(request, response);
    }
    
    /**
   * Check to see if a file path exists on system
   * 
   * @param path String representing full file path
   * @return true if file exists otherwise false
   */
  
  private boolean isFile(String path) {
    File aFile = new File(path);
    return aFile.exists();
  }

  /**
   * Method returns the classic location under WEB-INF for the file product.txt
   * unless on OpenShift. If on OpenShfit then looks up data directory pointed
   * to by environment variable OPENSHIFT_DATA_DIR
   *
   * @return Full path name of product.txt
   */
  private String getActualFile() {
    String path = "";
    String filename = "products.txt";
    String env = System.getenv("OPENSHIFT_DATA_DIR");
    if ((env != null) && (env.length() > 1)) {
      path = env + filename;
    } else {
      ServletContext sc = getServletContext();
      path = sc.getRealPath("/WEB-INF/" + filename);
    }
    return path;
  }
    
}