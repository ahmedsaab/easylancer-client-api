# Easylancer Client API

This is the API that will be used to serve the mobile app of Easylancer. It is the entry point for user driven actions and events and it utilizes other microservices like the data api, search api, payment api, authentication api, etc.

This Api also responsible for user's authotization.

## Search Page:
Page that shows some tasks that the user is interested in as well as the ability to search and filter all tasks open.

- **GET /search/all** -> Fetches summary for all tasks

## Task Page:
Page that shows the detailed view of a specific task as well as the actions the user can make on it

- **GET /tasks/:id/view** -> Fetches a detailed view (depending on logged in user) for a specific task
- **GET /tasks/:id/offers** -> Fetches all the offers made to a specific task that the logged in user is authorized to see

- **POST /tasks/create** -> Creates a new task for the logged in user
- **PUT /tasks/:id/edit** -> Edits a user's task information
- **POST /tasks/:id/apply** -> Apply the user to a specific task
- **POST /tasks/:id/review** -> Give a review for a specific task that is in in-progress and is assigned or created by the user (This endpoint is responsible for moving tasks to done, not-done, or investigate)
- **POST /tasks/:id/cancel** -> Cancel a specific task created by or assigned to the user

## Profile Page:
Page that shows the detailed view of a user profile and the actions that can be done on it

- **GET /profiles/:id/view** -> Fetches a detailed view for a specific user
- **GET /profiles/:id/tasks/assigned** -> Fetches a list of all the tasks assigned to this user
- **GET /profiles/:id/tasks/created** -> Fetches a list of all the tasks created by this user
- **GET /profiles/:id/reviews** -> Fetches all the reviews made to the tasks assigned to this user

- **PUT /profiles/:id/edit** -> Edits a the user's profile information
- **POST /profiles/:id/approve** -> Saves the user's uploaded ID document for approval

## User Page:
Page that shows user's private information and settings and the actions that can be done on them

## My Tasks Page:
Page that shows the user's related tasks which includes the ones they created, applied, assigned, finished, & cancelled

## Chats Page:
Page that shows the user's chats

## Message Page:
Page that shows the messages in a chat

## Authentication Page:
Page that logs in and sign up a user