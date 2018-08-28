public static String getTextContent(Node node) throws DOMException
{
String textContent = "";

if (node.getNodeType() == Node.ATTRIBUTE_NODE)
{
textContent = node.getNodeValue(); 
}
else
{
Node child = node.getFirstChild();
if (child != null)
{
Node sibling = child.getNextSibling();
if (sibling != null)
{
StringBuffer sb = new StringBuffer();
getTextContent(node, sb);
textContent = sb.toString();
}
else
{ 
if (child.getNodeType() == Node.TEXT_NODE) 
{
textContent = child.getNodeValue();
}
else
{
textContent = getTextContent(child);
}
}
}
}

return textContent;
}


private static void getTextContent(Node node, StringBuffer sb) throws DOMException
{
    Node child = node.getFirstChild();
    while (child != null)
    {
if (child.getNodeType() == Node.TEXT_NODE) 
{
sb.append(child.getNodeValue());
}
else
{
getTextContent(child, sb);
}
        child = child.getNextSibling();
    }
} 


public static void setTextContent(Node node, String textContent) throws DOMException
{
if (node.getNodeType() == Node.ATTRIBUTE_NODE)
{
if (textContent == null) textContent = "";
node.setNodeValue(textContent); 
}
else
{
Node child;
        while ((child = node.getFirstChild()) != null)
        {
            node.removeChild(child);
        }
        
        if (!StringUtils.isEmpty(textContent))
        {
Text textNode = node.getOwnerDocument().createTextNode(textContent);
node.appendChild(textNode);
        }
}
}