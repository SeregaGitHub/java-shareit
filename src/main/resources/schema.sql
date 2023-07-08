CREATE TABLE IF NOT EXISTS "USERS" (
  "USER_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  "USER_NAME" varchar(64) NOT NULL,
  "USER_EMAIL" varchar(128) NOT NULL,
  CONSTRAINT "PK_USERS_USER_ID" PRIMARY KEY ("USER_ID"),
  CONSTRAINT "UK_USERS_USER_EMAIL" UNIQUE ("USER_EMAIL")
);

CREATE TABLE IF NOT EXISTS "REQUEST" (
  "REQUEST_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  "REQUEST_DESCRIPTION" TEXT(3000) NOT NULL,
  "CREATED" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "USER_ID" INTEGER NOT NULL,
  CONSTRAINT "PK_REQUEST_REQUEST_ID" PRIMARY KEY ("REQUEST_ID"),
  CONSTRAINT "FK_REQUEST_USER_ID" FOREIGN KEY ("USER_ID")
            REFERENCES "USERS" ("USER_ID") ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS "ITEM" (
  "ITEM_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  "ITEM_NAME" varchar(64) NOT NULL,
  "ITEM_DESCRIPTION" varchar(1024) NOT NULL,
  "ITEM_AVAILABLE" boolean NOT NULL,
  "USER_ID" INTEGER NOT NULL,
  "REQUEST_ID" INTEGER,
  CONSTRAINT "PK_ITEM_ITEM_ID" PRIMARY KEY ("ITEM_ID"),
  CONSTRAINT "FK_ITEM_USER_ID" FOREIGN KEY ("USER_ID")
        REFERENCES "USERS" ("USER_ID") ON DELETE CASCADE,
  CONSTRAINT "FK_ITEM_REQUEST_ID" FOREIGN KEY ("REQUEST_ID")
          REFERENCES "REQUEST" ("REQUEST_ID"),
  CONSTRAINT "CK_ITEM_REQUEST_ID" CHECK ("REQUEST_ID" > 0)
);

CREATE TABLE IF NOT EXISTS "BOOKING" (
  "BOOKING_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  "START_TIME" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "END_TIME" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  "ITEM_ID" INTEGER NOT NULL,
  "USER_ID" INTEGER NOT NULL,
  "STATUS" VARCHAR(8) NOT NULL,
  CONSTRAINT "PK_BOOKING_BOOKING_ID" PRIMARY KEY ("BOOKING_ID"),
  CONSTRAINT "FK_BOOKING_ITEM_ID" FOREIGN KEY ("ITEM_ID")
        REFERENCES "ITEM" ("ITEM_ID") ON DELETE CASCADE,
  CONSTRAINT "FK_BOOKING_USER_ID" FOREIGN KEY ("USER_ID")
        REFERENCES "USERS" ("USER_ID") ON DELETE CASCADE,
  CONSTRAINT "CK_BOOKING_STATUS" CHECK ("STATUS" IN ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED'))
);

CREATE TABLE IF NOT EXISTS "COMMENT" (
  "COMMENT_ID" INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
  "TEXT" TEXT(3000) NOT NULL,
  "ITEM_ID" INTEGER NOT NULL,
  "USER_ID" INTEGER NOT NULL,
  "CREATED" TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT "PK_COMMENT_COMMENT_ID" PRIMARY KEY ("ITEM_ID"),
  CONSTRAINT "FK_COMMENT_ITEM_ID" FOREIGN KEY ("ITEM_ID")
          REFERENCES "ITEM" ("ITEM_ID") ON DELETE CASCADE,
  CONSTRAINT "FK_COMMENT_USER_ID" FOREIGN KEY ("USER_ID")
          REFERENCES "USERS" ("USER_ID") ON DELETE CASCADE
);