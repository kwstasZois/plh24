/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package project;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author kostas
 */
@Entity
@Table(name = "ARTICLE")
@NamedQueries({
    @NamedQuery(name = "Article.findAll", query = "SELECT a FROM Article a"),
    @NamedQuery(name = "Article.findByPageid", query = "SELECT a FROM Article a WHERE a.pageid = :pageid"),
    @NamedQuery(name = "Article.findByTitle", query = "SELECT a FROM Article a WHERE a.title = :title"),
    @NamedQuery(name = "Article.findByRate", query = "SELECT a FROM Article a WHERE a.rate = :rate"),
    @NamedQuery(name = "Article.findByTimestamp", query = "SELECT a FROM Article a WHERE a.timestamp = :timestamp")})
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "PAGEID")
    private Integer pageid;
    @Column(name = "TITLE")
    private String title;
    @Lob
    @Column(name = "SNIPPET")
    private String snippet;
    @Basic(optional = false)
    @Lob
    @Column(name = "COMMENTS")
    private String comments;
    @Column(name = "RATE")
    private Integer rate;
    @Column(name = "TIMESTAMP")
    private String timestamp;
    @JoinColumn(name = "CATEGORY", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Category category;

    public Article() {
    }

    public Article(Integer pageid) {
        this.pageid = pageid;
    }

    public Article(Integer pageid, String comments) {
        this.pageid = pageid;
        this.comments = comments;
    }

    public Article(Integer pageid, String title, String snippet, String comments, Integer rate, String timestamp) {
        this.pageid = pageid;
        this.title = title;
        this.snippet = snippet;
        this.comments = comments;
        this.rate = rate;
        this.timestamp = timestamp;
        
       
    }

    public Article(Integer pageid, String title, String snippet, String comments, Integer rate, String timestamp, Category category) {
        this.pageid = pageid;
        this.title = title;
        this.snippet = snippet;
        this.comments = comments;
        this.rate = rate;
        this.timestamp = timestamp;
        this.category = category;
    }
    
    

    public Integer getPageid() {
        return pageid;
    }

    public void setPageid(Integer pageid) {
        this.pageid = pageid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (pageid != null ? pageid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Article)) {
            return false;
        }
        Article other = (Article) object;
        if ((this.pageid == null && other.pageid != null) || (this.pageid != null && !this.pageid.equals(other.pageid))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.title;
        //return "project.Article[ pageid=" + pageid + " ]";
    }
    
}
