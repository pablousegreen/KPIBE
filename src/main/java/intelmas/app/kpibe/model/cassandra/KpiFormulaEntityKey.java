package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cassandra.core.Ordering;
import org.springframework.cassandra.core.PrimaryKeyType;
import org.springframework.data.cassandra.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.mapping.PrimaryKeyColumn;

@PrimaryKeyClass
public class KpiFormulaEntityKey implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;
	
	@PrimaryKeyColumn(name = "organisation", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
	private String organisation;

	@PrimaryKeyColumn(name = "category", ordinal = 1, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String category;
	
	@PrimaryKeyColumn(name = "name", ordinal = 2, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String name;
	
	@PrimaryKeyColumn(name = "nodeversion", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering = Ordering.DESCENDING)
	private String nodeversion;
	
	public KpiFormulaEntityKey() {
	}
	
	public KpiFormulaEntityKey(String id) {
		this.buildKeyFromId(id);
	}
	
	public KpiFormulaEntityKey(String organisation, String category, String name, String nodeversion) {
		this.setOrganisation(organisation);
		this.setCategory(category);
		this.setName(name);
		this.setNodeversion(nodeversion);
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getOrganisation() {
		return organisation;
	}
	
	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}
	
	public String getNodeversion() {
		return nodeversion;
	}
	
	public void setNodeversion(String nodeversion) {
		this.nodeversion = nodeversion;
	}
	
	public String generateId(){
		StringBuilder idBuilder = new StringBuilder();
		idBuilder
			.append(this.getOrganisation()).append("-;-")
			.append(this.getCategory()).append("-;-")
			.append(this.getName()).append("-;-")
			.append(this.getNodeversion());
		
		try {
			return new String( Base64.encodeBase64(idBuilder.toString().getBytes("UTF-8")), StandardCharsets.UTF_8 );
		} catch (UnsupportedEncodingException e) {
			return new String(Base64.encodeBase64(idBuilder.toString().getBytes()));
		}
	}
	
	public void buildKeyFromId(String id){
		
		String decodedId = new String(Base64.decodeBase64(id));
		try{
			decodedId = new String(Base64.decodeBase64(id), Charset.forName("UTF-8"));
		}catch(Exception e){}
		
		String[] parsedIds = StringUtils.splitByWholeSeparator(decodedId, "-;-");
		
		if(parsedIds == null || parsedIds.length != 4 ) return;
		
		this.organisation = parsedIds[0];
		this.category = parsedIds[1];
		this.name = parsedIds[2];
		this.nodeversion = parsedIds[3];
	
	}

}
