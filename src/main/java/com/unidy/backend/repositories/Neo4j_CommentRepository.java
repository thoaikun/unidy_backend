package com.unidy.backend.repositories;

import com.unidy.backend.domains.dto.responses.CommentResponse;
import com.unidy.backend.domains.entity.neo4j.CommentNode;
import jakarta.transaction.Transactional;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
public interface Neo4j_CommentRepository extends Neo4jRepository<CommentNode,Integer> {
    CommentNode findCommentNodeByCommentId(int commentId);

    @Query("""
            MATCH (userPost:user)-[:HAS_POST]->(post:post {post_id: $postId})
            OPTIONAL MATCH (post)-[:HAS_COMMENT]->(comment:comment)<-[:WROTE_COMMENT]-(user:user)
            OPTIONAL MATCH (comment)<-[:REPLY_COMMENT]-(commentReply:comment)
            WITH userPost, post, comment, collect(DISTINCT commentReply) AS replies,user
            WITH user, post, comment, replies,
                 CASE WHEN comment IS NULL THEN false ELSE true END AS hasComment
            WHERE hasComment
            RETURN user AS user, post, comment,
                       CASE WHEN size(replies) > 0 THEN true ELSE false END AS haveReply
            ORDER BY comment.create_date DESC
            SKIP $skip
            LIMIT $limit;
            """)
    List<CommentResponse> getAllCommentByPostId(@Param("postId") String postId, @Param("skip") int skip, @Param("limit") int limit);

    @Query("""
                MATCH (comment:comment {comment_id: $commentId})
                OPTIONAL MATCH (replyComment:comment)-[reply:REPLY_COMMENT]->(comment)
                OPTIONAL MATCH (replyComment_2:comment)-[reply_2:REPLY_COMMENT]->(replyComment)
                OPTIONAL MATCH (replyComment)-[:WROTE_COMMENT]-(userReply:user)
                WITH comment, replyComment, collect(DISTINCT replyComment_2) AS replies, userReply
                WITH comment, replyComment, replies, userReply,
                     CASE WHEN replyComment IS NULL THEN false ELSE true END AS hasReplyComment
                WHERE hasReplyComment
                RETURN replyComment AS comment,
                       size(replies) > 0 AS haveReply,
                       userReply AS user
                ORDER BY comment.comment_id
                SKIP $skip
                LIMIT $limit;
            """)
    List<CommentResponse> getAllReplyComment(Integer commentId, int skip, int limit);

    @Query("""
            MATCH (userPost: user) - [has_post: HAS_CAMPAIGN] -> (campaign:campaign {campaign_id : $campaignId})
            OPTIONAL MATCH (campaign)-[:HAS_COMMENT]->(comment:comment)<-[:WROTE_COMMENT]-(user:user)
            OPTIONAL MATCH (comment)<-[:REPLY_COMMENT]-(commentReply:comment)
            WITH user, comment, collect(DISTINCT commentReply) AS replies
            WITH user, comment, replies,
                 CASE WHEN comment IS NULL THEN false ELSE true END AS hasComment
            WHERE hasComment
            RETURN user, comment,
                   size(replies) > 0 AS haveReply
            ORDER BY comment.comment_id
            SKIP $skip
            LIMIT $limit;
    """)
    List<CommentResponse> getAllCommentByCampaignId(@Param("campaignId") String campaignId, @Param("skip") int skip, @Param("limit") int limit);
}
