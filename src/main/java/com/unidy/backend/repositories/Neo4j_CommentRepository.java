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
                MATCH (userPost: user) - [has_post: HAS_POST] -> (post:post {post_id : $postId})
                OPTIONAL MATCH (post) - [has_comment: HAS_COMMENT] -> (comment :comment) <-[wrote_comment:WROTE_COMMENT] -(user : user)
                OPTIONAL MATCH (comment) <- [reply :REPLY_COMMENT] - (commentReply: comment )
                RETURN user, comment, CASE WHEN reply IS NOT NULL THEN true ELSE false END AS haveReply        ORDER BY comment.comment_id
                SKIP $skip
                LIMIT $limit;
            """)
    List<CommentResponse> getAllCommentByPostId(@Param("postId") String postId, @Param("skip") int skip, @Param("limit") int limit);

    @Query("""
            OPTIONAL MATCH (comment: comment {comment_id : $commentId}) <- [reply :REPLY_COMMENT] - (replyComment: comment )
            OPTIONAL MATCH (replyComment) - [:WROTE_COMMENT] - (userReply :user)\s
            RETURN replyComment as comment, CASE WHEN reply IS NOT NULL THEN true ELSE false END AS haveReply,userReply as user
            ORDER BY comment.comment_id
            SKIP $skip
            LIMIT $limit;
            """)
    List<CommentResponse> getAllReplyCommentByPostId(int commentId, int skip, int limit);
}
