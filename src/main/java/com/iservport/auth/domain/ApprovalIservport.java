package com.iservport.auth.domain;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.oauth2.common.util.JsonDateDeserializer;
import org.springframework.security.oauth2.common.util.JsonDateSerializer;
import org.springframework.security.oauth2.provider.approval.Approval;

/**
 * 
 * Create a persistent Entity to approvals. Contain code from {@link Approval }.
 * 
 * @author Eldevan Nery Jr
 *
 */
@javax.persistence.Entity
@Table(name = "oauth_approvals")
public class ApprovalIservport implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private String userId;

	private String clientId;

	private String scope;

	public enum ApprovalStatus {
		APPROVED,
		DENIED;
	}

	@Enumerated(EnumType.STRING)
	private ApprovalStatus status;
	
	private Date expiresAt;

	private Date lastUpdatedAt;
	
	private Date lastModifiedAt;

	public ApprovalIservport(String userId, String clientId, String scope, int expiresIn, ApprovalStatus status) {
		this(userId, clientId, scope, new Date(), status, new Date());
		Calendar expiresAt = Calendar.getInstance();
		expiresAt.add(Calendar.MILLISECOND, expiresIn);
		setExpiresAt(expiresAt.getTime());
	}

	public ApprovalIservport(String userId, String clientId, String scope, Date expiresAt, ApprovalStatus status) {
		this(userId, clientId, scope, expiresAt, status, new Date());
	}

	public ApprovalIservport(String userId, String clientId, String scope, Date expiresAt, ApprovalStatus status, Date lastUpdatedAt) {
		this.userId = userId;
		this.clientId = clientId;
		this.scope = scope;
		this.expiresAt = expiresAt;
		this.status = status;
		this.lastUpdatedAt = lastUpdatedAt;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId == null ? "" : userId;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId == null ? "" : clientId;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope == null ? "" : scope;
	}

	@JsonSerialize(using = JsonDateSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
	public Date getExpiresAt() {
		return expiresAt;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setExpiresAt(Date expiresAt) {
		if (expiresAt == null) {
			Calendar thirtyMinFromNow = Calendar.getInstance();
			thirtyMinFromNow.add(Calendar.MINUTE, 30);
			expiresAt = thirtyMinFromNow.getTime();
		}
		this.expiresAt = expiresAt;
	}

	@JsonSerialize(using = JsonDateSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
	public Date getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setLastUpdatedAt(Date lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}
	
	@JsonSerialize(using = JsonDateSerializer.class, include = JsonSerialize.Inclusion.NON_NULL)
	public Date getLastModifiedAt() {
		return lastModifiedAt;
	}
	
	@JsonDeserialize(using = JsonDateDeserializer.class)
	public void setLastModifiedAt(Date lastModifiedAt) {
		this.lastModifiedAt = lastModifiedAt;
	}

	@JsonIgnore
	public boolean isCurrentlyActive() {
		return expiresAt != null && expiresAt.after(new Date());
	}

	@JsonIgnore
	public boolean isApproved() {
		return isCurrentlyActive() && status==ApprovalStatus.APPROVED;
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public ApprovalStatus getStatus() {
		return status;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientId == null) ? 0 : clientId.hashCode());
		result = prime * result
				+ ((expiresAt == null) ? 0 : expiresAt.hashCode());
		result = prime * result + id;
		result = prime * result
				+ ((lastUpdatedAt == null) ? 0 : lastUpdatedAt.hashCode());
		result = prime * result + ((scope == null) ? 0 : scope.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApprovalIservport other = (ApprovalIservport) obj;
		if (clientId == null) {
			if (other.clientId != null)
				return false;
		} else if (!clientId.equals(other.clientId))
			return false;
		if (expiresAt == null) {
			if (other.expiresAt != null)
				return false;
		} else if (!expiresAt.equals(other.expiresAt))
			return false;
		if (id != other.id)
			return false;
		if (lastUpdatedAt == null) {
			if (other.lastUpdatedAt != null)
				return false;
		} else if (!lastUpdatedAt.equals(other.lastUpdatedAt))
			return false;
		if (scope == null) {
			if (other.scope != null)
				return false;
		} else if (!scope.equals(other.scope))
			return false;
		if (status != other.status)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return String.format("[%s, %s, %s, %s, %s, %s]", userId, scope, clientId, expiresAt, status.toString(), lastUpdatedAt);
	}

}
