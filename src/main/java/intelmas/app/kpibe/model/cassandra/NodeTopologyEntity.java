package intelmas.app.kpibe.model.cassandra;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;


@Table("nodes_by_oss")
public class NodeTopologyEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8325328907318698346L;

	@PrimaryKey("node_name")
	private String nodeName;
	
	@Column("node_datetime")
	private Date nodeDatetime;
	
	@Column("node_fqdn_name")
	private String moid;
	
	@Column("node_ip_address")
	private String nodeIpAddress;
	
	@Column("node_lat")
	private String nodeLatitude;
	
	@Column("node_long")
	private String nodeLongitude;
	
	@Column("node_parent")
	private String oss;
	
	@Column("node_region")
	private String region;
	
	@Column("node_tech")
	private String tech;
	
	@Column("node_type")
	private String type;
	
	@Column("node_version")
	private String nodeVersion;
	
	public NodeTopologyEntity() {
	}
	
	public String getMoid() {
		return moid;
	}
	
	public void setMoid(String moid) {
		this.moid = moid;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public Date getNodeDatetime() {
		return nodeDatetime;
	}

	public void setNodeDatetime(Date nodeDatetime) {
		this.nodeDatetime = nodeDatetime;
	}

	public String getNodeIpAddress() {
		return nodeIpAddress;
	}

	public void setNodeIpAddress(String nodeIpAddress) {
		this.nodeIpAddress = nodeIpAddress;
	}

	public String getNodeLatitude() {
		return nodeLatitude;
	}

	public void setNodeLatitude(String nodeLatitude) {
		this.nodeLatitude = nodeLatitude;
	}

	public String getNodeLongitude() {
		return nodeLongitude;
	}

	public void setNodeLongitude(String nodeLongitude) {
		this.nodeLongitude = nodeLongitude;
	}

	public String getOss() {
		return oss;
	}

	public void setOss(String oss) {
		this.oss = oss;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getTech() {
		return tech;
	}

	public void setTech(String tech) {
		this.tech = tech;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getNodeVersion() {
		return nodeVersion;
	}

	public void setNodeVersion(String nodeVersion) {
		this.nodeVersion = nodeVersion;
	}
	
	
}
