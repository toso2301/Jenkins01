import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLReader{
	
	private Document xmlDocment = null;
	private File file = null;
	protected Document getDocument(){
		return xmlDocment;
	}
	protected void setDocument(Document xmlDocment){
		this.xmlDocment = xmlDocment;
	}
	protected File getFile(){
		return this.file;
	}
	protected void setFile(String filepath){
		file = new File(filepath);
	}
	protected enum NodeType{
		NODE,
		ATTRIBUTE,
		LIST,
		
		RET_RESULT,
		RET_COUNT,
		RET_LISTRESULT
	}
	public static XMLReader getInstance(String path) throws Exception{
		return new XMLReader(path);
	}
	protected XMLReader(){ }
	protected XMLReader(String path) throws Exception{ 
		initialize(path);
	}
	protected void initialize(String path)throws Exception{
		setFile(path);
		DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuild = docBuildFact.newDocumentBuilder();
		setDocument(docBuild.parse(getFile()));
	}
	protected Object NodeFind(String xpath,NodeType flowtype) throws Exception{
		NodeList nodeList = null;
		Element element = null;
		getDocument().getDocumentElement().normalize();
		ArrayList<String> pathNode = PathSplite(xpath);
		NodeType type = null;
		int listIndex = 0;
		String pNodeName = "";
		for(int i=0 ; i < pathNode.size() ; i++){
			type = NodeTypeCheck(pathNode.get(i));
			if(type.equals(NodeType.NODE) || type.equals(NodeType.LIST)){
				pNodeName = pathNode.get(i);
				if(type.equals(NodeType.NODE)){
					listIndex = 0;
					if(	(flowtype == NodeType.RET_COUNT || flowtype==NodeType.RET_LISTRESULT) 
							&& i == pathNode.size() -1)
						break;
				}else{
					try{
						int sPos = pNodeName.indexOf("[")+1;
						int ePos = pNodeName.indexOf("]");
						listIndex = Integer.valueOf(pNodeName.substring(sPos,ePos)) - 1;
						pNodeName = pNodeName.substring(0,sPos-1);
						if(i == pathNode.size() -1) break;
					}catch(Exception e){
						throw new Exception("Pathの表現が非性格です。ErrorCode : FE_NodeFind - 1");
					}
				}
				
				if(element == null)	{
					nodeList = getDocument().getElementsByTagName(pNodeName);
				}else{
					nodeList = element.getElementsByTagName(pNodeName);
				}
				if(nodeList.getLength() > 0){
					element = (Element)nodeList.item(listIndex);
				}else{
					throw new Exception("Pathの表現が非性格です。ErrorCode : FE_NodeFind - 2");
				}
			}
		}
		if(type.equals(NodeType.NODE) ||type.equals(NodeType.LIST)){
			if(element.getChildNodes().getLength() > 0){
				nodeList = element.getChildNodes();
				if(nodeList.getLength() <= 1){
					Node node = (Node)nodeList.item(0);
					if(NodeType.RET_RESULT == flowtype){
						return node.getNodeValue();
					}else{
						return 1;
					}
				}else{
					ArrayList<String> pRet = new ArrayList<String>();
					nodeList = element.getElementsByTagName(pNodeName);
					if(NodeType.RET_LISTRESULT == flowtype){
						for(int i=0;i<nodeList.getLength();i++){
							element = (Element)nodeList.item(i);
							if(element.getChildNodes().getLength() <= 1){
								Node node = (Node)element.getChildNodes().item(0);
								pRet.add(node.getNodeValue());
							}
						}
						return pRet;
					}else{
						return nodeList.getLength();
					}
				}
			}else{
				throw new Exception("動作エラー。ErrorCode : FE_NodeFind");
			}
		}else if(type.equals(NodeType.ATTRIBUTE)){
			if(NodeType.RET_RESULT == flowtype){
				String attrName = pathNode.get(pathNode.size()-1).substring(1);
				return element.getAttribute(attrName);
			}
		}
		return null;
	}
	protected int getCountChildNode(String xpath,String childenode){
		try{
			return Integer.valueOf(NodeFind(xpath+"/"+childenode,NodeType.RET_COUNT).toString());
		}catch(Exception e){
			throw new ClassCastException("ノードタイプが合わないです。");
		}
	}
	protected NodeType NodeTypeCheck(String nodeName){
		if(nodeName.indexOf("@") >= 0){
			return NodeType.ATTRIBUTE;
		}else if(nodeName.indexOf("[") >= 0 && nodeName.indexOf("]") >= 0){
			return NodeType.LIST;
		}else{
			return NodeType.NODE;
		}
	}
	protected ArrayList<String> PathSplite(String path) throws Exception{
		String[] pathNode = path.split("/");
		if(pathNode.length <= 1){
			throw new Exception("Pathの表現が非性格です。- ErrorCode : PathSplite");
		}
		ArrayList<String> ret = new ArrayList<String>();
		for(int i=1 ; i < pathNode.length ; i++){
			ret.add(pathNode[i]);
		}
		return ret;
	}
	@SuppressWarnings("rawtypes")
	public Object getValue(String xpath,Class type) throws ClassCastException,Exception{
		try{
			if(type == String.class){
				return NodeFind(xpath,NodeType.RET_RESULT);
			}else if(type == ArrayList.class){
				return NodeFind(xpath,NodeType.RET_LISTRESULT);
			}else{
				throw new ClassCastException();
			}
		}catch(ClassCastException e){
			throw new ClassCastException("「" + type.getName() +"」タイプに返却できません。- ErrorCode : get");
		}
	}
}
