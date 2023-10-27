-- CREATE DATABASE unidy_database;

CREATE TABLE user (
                      user_id         BIGINT   AUTO_INCREMENT     PRIMARY KEY ,
                      full_name       VARCHAR(30)        ,
                      address         VARCHAR(100)       ,
                      day_of_birth    DATE               ,
                      sex             VARCHAR(6)         ,
                      phone           VARCHAR(15)        ,
                      email           VARCHAR(30)        ,
                      job             VARCHAR(50)        ,
                      work_location   VARCHAR(100)       ,
                      password        VARCHAR(255)       ,
                      role            VARCHAR(255)
);



CREATE TABLE sponsor (
                         sponsor_id         INTEGER  AUTO_INCREMENT   PRIMARY KEY ,
                         sponsor_name       VARCHAR(30)      NOT NULL ,
                         phone           VARCHAR(15)         ,
                         email           VARCHAR(30)         NOT NULL ,
                         status          VARCHAR(10)
);

CREATE TABLE organization (
                              organization_id         INTEGER    AUTO_INCREMENT  PRIMARY KEY ,
                              organization_name       VARCHAR(30)         not null ,
                              address                 VARCHAR(100)        not null ,
                              phone                   VARCHAR(15)         not null ,
                              email                   VARCHAR(30)         not null ,
                              status                  VARCHAR(10)         ,
                              country                 VARCHAR(20)
);


CREATE TABLE volunteer (
                           volunteer_id        BIGINT   AUTO_INCREMENT  PRIMARY KEY ,
                           user_id             VARCHAR(30)      not null ,
                           sponsor_íd          INTEGER           not null ,
                           organization_id     INTEGER           not null ,
                           FOREIGN KEY (sponsor_íd) REFERENCES sponsor(sponsor_id),
                           FOREIGN KEY (organization_id) REFERENCES organization(organization_id)
);

CREATE TABLE token (
                       id          INTEGER     AUTO_INCREMENT    not null,
                       token       VARCHAR(255)    unique,
                       token_type  VARCHAR(255)    check (token_type in ('BEARER')),
                       expired     BOOLEAN         not null,
                       revoked     BOOLEAN         not null,
                       user_id     BIGINT,
                       FOREIGN KEY (user_id) REFERENCES user(user_id),
                       primary key (id)
);


CREATE TABLE notification (
                              notify_id       BIGINT   AUTO_INCREMENT  PRIMARY KEY  ,
                              content         VARCHAR(30)             ,
                              time_create     VARCHAR(15)             ,
                              user_id         BIGINT                 ,
                              FOREIGN KEY (user_id) REFERENCES user(user_id)
);

CREATE TABLE feed_back (
                           fb_id           BIGINT  AUTO_INCREMENT   PRIMARY KEY  ,
                           content         VARCHAR(30)             NOT NULL ,
                           time_create     VARCHAR(15)             ,
                           user_id         BIGINT                 ,
                           FOREIGN KEY (user_id) REFERENCES user(user_id)
);



CREATE TABLE campaign(
                         campaign_id         INTEGER  AUTO_INCREMENT   PRIMARY KEY ,
                         content             VARCHAR(1000)       NOT NULL ,
                         numbers_volunteer   INTEGER             NOT NULL ,
                         start_day           DATE                NOT NULL ,
                         end_day             DATE                NOT NULL ,
                         location            VARCHAR(255)        ,
                         status              VARCHAR(30)         ,
                         create_day          DATE                ,
                         update_day          DATE                ,
                         update_by           VARCHAR(255)
);

CREATE TABLE image_campaign (
                                image_id        INTEGER     PRIMARY KEY,
                                link_image      VARCHAR(255) NOT NULL ,
                                campaign_id     INTEGER,
                                FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)
);

CREATE TABLE sponsor_location (
                                  location_id     INTEGER     PRIMARY KEY ,
                                  address         VARCHAR(255)    ,
                                  sponsor_id      INTEGER         ,
                                  FOREIGN KEY (sponsor_id) REFERENCES sponsor(sponsor_id)
);


CREATE TABLE organize_campaign (
                                   campaign_id      INTEGER     PRIMARY KEY ,
                                   organize_id      INTEGER     ,
                                   rating_point     INTEGER     ,
                                   FOREIGN KEY (organize_id) REFERENCES organization(organization_id)
);


CREATE TABLE volunteer_join_campaign (
                                         volunteer_id    BIGINT ,
                                         campaign_id     INTEGER ,
                                         time_join       DATE    ,
                                         status          VARCHAR(10),

                                         PRIMARY KEY (volunteer_id, campaign_id),
                                         FOREIGN KEY (volunteer_id) REFERENCES volunteer(volunteer_id),
                                         FOREIGN KEY (campaign_id)  REFERENCES campaign(campaign_id)
);

CREATE TABLE certificate(
                            certificate_id  INTEGER     PRIMARY KEY ,
                            file            VARCHAR(255)
);

CREATE TABLE volunteer_certificate (
                                       volunteer_id    BIGINT,
                                       certificate_id  INTEGER,
                                       campaign_id     INTEGER,
                                       PRIMARY KEY (volunteer_id,certificate_id),
                                       FOREIGN KEY (campaign_id) REFERENCES campaign(campaign_id)
);


CREATE TABLE post (
                      post_id     INTEGER     PRIMARY KEY ,
                      content     VARCHAR(255)    NOT NULL ,
                      create_date DATE            ,
                      update_date DATE            ,
                      is_block    BOOLEAN
);

CREATE TABLE post_image (
                            image_id        INTEGER     PRIMARY KEY,
                            link_image      VARCHAR(255)    NOT NULL,
                            post_id         INTEGER,
                            FOREIGN KEY (post_id) REFERENCES post(post_id)
);

CREATE TABLE post_log (
                          post_log_id     INTEGER     ,
                          post_id         INTEGER     ,
                          type_log        VARCHAR(15) ,
                          log_content     VARCHAR(255),
                          create_time     DATE        ,
                          PRIMARY KEY (post_log_id,post_id)
);


CREATE TABLE volunteer_post (
                                post_id         INTEGER     PRIMARY KEY ,
                                volunteer_id    BIGINT,
                                FOREIGN KEY (post_id) REFERENCES post(post_id),
                                FOREIGN KEY (volunteer_id)  REFERENCES volunteer(volunteer_id)
);

CREATE TABLE comment (
                         comment_id      INTEGER         PRIMARY KEY ,
                         content         VARCHAR(255)    NOT NULL ,
                         create_time     DATE            ,
                         is_block        BOOLEAN         ,
                         reply_by_comment    INTEGER     ,
                         FOREIGN KEY (reply_by_comment) REFERENCES comment(comment_id)
);

CREATE TABLE comment_log (
                             comment_log_id  INTEGER ,
                             comment_id      INTEGER ,
                             type_log        VARCHAR(15),
                             log_content     VARCHAR(255),
                             create_time     DATE        ,
                             PRIMARY KEY (comment_log_id,comment_id)
);


CREATE TABLE comment_in_post (
                                 volunteer_id    BIGINT ,
                                 post_id         INTEGER ,
                                 comment_id      INTEGER ,
                                 PRIMARY KEY (volunteer_id,post_id),
                                 FOREIGN KEY (comment_id) REFERENCES comment(comment_id)
);

CREATE TABLE campaign_post (
                               post_id     INTEGER     PRIMARY KEY ,
                               campaign_id INTEGER     ,
                               FOREIGN KEY (post_id) REFERENCES post(post_id)
);

CREATE TABLE reaction (
                          user_id     BIGINT ,
                          post_id     INTEGER ,
                          react_time  DATE    ,
                          react_type  VARCHAR(15),
                          PRIMARY KEY (user_id,post_id),
                          FOREIGN KEY (user_id)  REFERENCES user(user_id),
                          FOREIGN KEY (post_id)  REFERENCES post(post_id)
);

CREATE TABLE achievement (
                             achievement_id   INTEGER    PRIMARY KEY ,
                             volunteer_id     BIGINT        ,
                             content          VARCHAR(255)   ,
                             FOREIGN KEY (volunteer_id)  REFERENCES volunteer(volunteer_id)
);

CREATE TABLE volunteer_achievement (
                                       achievement_id  INTEGER ,
                                       volunteer_id    BIGINT ,
                                       PRIMARY KEY (achievement_id,volunteer_id),
                                       FOREIGN KEY (achievement_id) REFERENCES achievement(achievement_id),
                                       FOREIGN KEY (volunteer_id)   REFERENCES volunteer(volunteer_id)
);

CREATE TABLE transaction (
                             transaction_id      INTEGER     PRIMARY KEY ,
                             transaction_type    VARCHAR(30) ,
                             transaction_time    DATE        ,
                             transaction_amount  BIGINT
);

CREATE TABLE sponsor_transaction (
                                     transaction_id      INTEGER     PRIMARY KEY ,
                                     sponsor_id          INTEGER     ,
                                     FOREIGN KEY (transaction_id) REFERENCES transaction(transaction_id),
                                     FOREIGN KEY (sponsor_id)     REFERENCES sponsor(sponsor_id)
);

CREATE TABLE organization_transaction (
                                          transaction_id      INTEGER     PRIMARY KEY ,
                                          organization_id          INTEGER     ,
                                          FOREIGN KEY (transaction_id)    REFERENCES transaction(transaction_id),
                                          FOREIGN KEY (organization_id)   REFERENCES organization(organization_id)
);

CREATE TABLE conversation (
                              conversation_id     INTEGER  ,
                              user_id_1           BIGINT  ,
                              user_id_2           BIGINT  ,
                              PRIMARY KEY (conversation_id,user_id_1,user_id_2),
                              FOREIGN KEY (user_id_1) REFERENCES user(user_id),
                              FOREIGN KEY (user_id_2) REFERENCES user(user_id)
);

CREATE TABLE otp (
    otp_id          INTEGER   AUTO_INCREMENT   PRIMARY KEY ,
    otp_code        VARCHAR(6)     ,
    otp_expired     BOOLEAN     ,
    user_id         BIGINT      ,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
);

-- # INSERT INTO user VALUES (1,'Trương Huy Thái', 'Gò Vấp', '2002-05-31', 'male', '0348273185', 'huythai31052002@gmail.com', 'Student', 'BKU','123456','VOLUNTEER');
-- # INSERT INTO user VALUES (2,'Lê Nguyễn Huyền Thoại', 'Thủ Đức', '2002-09-10', 'male', '0348273185', 'thoaile0910@gmail.com', 'Student', 'BKU','123456','VOLUNTEER');
-- # INSERT INTO user VALUES (3,'Nguyễn Hoàng Bảo Hùng', 'Đồng Nai', '2002-09-10', 'male', '0348273185', 'nhb.hung@gmail.com', 'Student', 'BKU','123456','VOLUNTEER');
-- #
-- # DELETE FROM user where user_id = 3 ;
