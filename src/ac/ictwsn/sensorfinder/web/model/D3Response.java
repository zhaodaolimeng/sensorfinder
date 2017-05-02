package ac.ictwsn.sensorfinder.web.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A wrapper class for d3js visualization
 * @author li
 *
 */
public class D3Response extends AjaxResponse{
	
	@JsonProperty("nodes")
	List<D3Node> nodes;
	@JsonProperty("links")
	List<D3Link> links;
	
	public D3Response(){
		this.nodes = new ArrayList<D3Node>();
		this.links = new ArrayList<D3Link>();
	}
	public void addNode(String id, Integer group){
		D3Node node = new D3Node(id, group);
		nodes.add(node);
	}
	public void addLink(String source, String target, Integer value){
		D3Link link = new D3Link(source, target, value);
		links.add(link);
	}
	public List<D3Node> getNodes() {
		return nodes;
	}
	public void setNodes(List<D3Node> nodes) {
		this.nodes = nodes;
	}
	public List<D3Link> getLinks() {
		return links;
	}
	public void setLinks(List<D3Link> links) {
		this.links = links;
	}
}

class D3Node{
	String id;
	Integer group;
	
	public D3Node(String id, Integer group){
		this.id = id;
		this.group = group;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getGroup() {
		return group;
	}
	public void setGroup(Integer group) {
		this.group = group;
	}
}

class D3Link{	
	String source;
	String target;
	Integer value;
	
	public D3Link(String source, String target, Integer value){
		this.source = source;
		this.target = target;
		this.value = value;
	}
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}	
}
