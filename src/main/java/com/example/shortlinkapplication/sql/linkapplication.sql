-- Create a new database called 'linkapplication'
-- Connect to the 'master' database to run this snippet
USE master
GO
-- Create the new database if it does not exist already
IF NOT EXISTS (
    SELECT [name]
        FROM sys.databases
        WHERE [name] = N'linkapplication'
)
CREATE DATABASE linkapplication
GO

CREATE TABLE [user_account] (
  userID INT IDENTITY(1,1) PRIMARY KEY,
  name NVARCHAR(20),
  email VARCHAR(32),
  password VARCHAR(50),
  auth_provider VARCHAR(50),
  email_verified BIT,
  provider_id VARCHAR(50)
);

CREATE TABLE url (
  shortLink VARCHAR(16) PRIMARY KEY,
  originalURL VARCHAR(MAX),
  creationDate DATETIME,
  expirationDate DATETIME,
  userID INT,
  FOREIGN KEY (userID) REFERENCES [user_account](userID)
);

CREATE TABLE project (
  projectID INT IDENTITY(1,1) PRIMARY KEY,
  projectName VARCHAR(20),
  projectSlug VARCHAR(20),
  creationDate DATETIME,
  shortLink VARCHAR(16),
  FOREIGN KEY (shortLink) REFERENCES url(shortLink)
);

CREATE TABLE locations (
  locationID INT IDENTITY(1,1) PRIMARY KEY,
  country VARCHAR(50),
  city VARCHAR(50)
);

CREATE TABLE devices (
  deviceID INT IDENTITY(1,1) PRIMARY KEY,
  device VARCHAR(50),
  browser VARCHAR(50)
);

CREATE TABLE analytics (
  chartID INT IDENTITY(1,1) PRIMARY KEY,
  totalClick INT,
  timeClick DATETIME,
  locationID INT,
  deviceID INT,
  projectID INT,
  FOREIGN KEY (locationID) REFERENCES locations(locationID),
  FOREIGN KEY (deviceID) REFERENCES devices(deviceID),
  FOREIGN KEY (projectID) REFERENCES project(projectID)
);

CREATE TABLE payments (
  transactionID INT IDENTITY(1,1) PRIMARY KEY,
  object VARCHAR(20),
  amount NUMERIC(10,2),
  created DATETIME,
  currency VARCHAR(5),
  status VARCHAR(10)
);

CREATE TABLE subscriptions (
  subscriptionID INT IDENTITY(1,1) PRIMARY KEY,
  userID INT,
  address VARCHAR(255),
  startDate DATETIME,
  endDate DATETIME,
  remainingTime INT
  FOREIGN KEY (userID) REFERENCES [user_account](userID)
);
CREATE TABLE [dbo].[confirm_token]
(
    [Id] INT IDENTITY(1,1) PRIMARY KEY, -- Primary Key column
    [token] VARCHAR(MAX) NOT NULL,
    [created_at] DATETIME NOT NULL,
    [expired_at] DATETIME NOT NULL,
    [confirmed_at] DATETIME NOT NULL
);


