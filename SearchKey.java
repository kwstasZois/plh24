/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author kostas
 */
@Entity
@Table(name = "SEARCH_KEY")
@NamedQueries({
    @NamedQuery(name = "SearchKey.findAll", query = "SELECT s FROM SearchKey s"),
    @NamedQuery(name = "SearchKey.findByKeyword", query = "SELECT s FROM SearchKey s WHERE s.keyword = :keyword"),
    @NamedQuery(name = "SearchKey.findBySearchCount", query = "SELECT s FROM SearchKey s WHERE s.searchCount = :searchCount")})
public class SearchKey implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "KEYWORD")
    private String keyword;
    @Basic(optional = false)
    @Column(name = "SEARCH_COUNT")
    private int searchCount;

    public SearchKey() {
    }

    public SearchKey(String keyword) {
        this.keyword = keyword;
    }

    public SearchKey(String keyword, int searchCount) {
        this.keyword = keyword;
        this.searchCount = searchCount;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(int searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (keyword != null ? keyword.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SearchKey)) {
            return false;
        }
        SearchKey other = (SearchKey) object;
        if ((this.keyword == null && other.keyword != null) || (this.keyword != null && !this.keyword.equals(other.keyword))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "project.SearchKey[ keyword=" + keyword + " ]";
    }
    
}
