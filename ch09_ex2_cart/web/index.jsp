<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Murach's Java Servlets and JSP</title>
    <link rel="stylesheet" href="styles/main.css" type="text/css"/>
    <%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
</head>
<body>
    
<h1>CD list</h1>
<table>
    <tr>
        <th>Description</th>
        <th class="right">Price</th>
        <th>&nbsp;</th>
    </tr>
    <c:forEach var="product" items= "${sessionScope.products}">
        <tr>
            <td><c:out value='${product.description}' /></td>
            <td class="right"> $<c:out value='${product.price}' /></td>
            <td><form action="cart" method="post">
                <input type="hidden" name="productCode" value="${product.code}">
                <input type="submit" value="Add To Cart">
            </form></td>
        </tr>
    </c:forEach>
</table>

        
</body>
</html>