# Easylancer Client API

This is the API that will be used to serve the mobile app of Easylancer. It is the entry point for user driven actions and events and it utilizes other microservices like the data api, search api, payment api, etc.

##Search Page:
Page that shows some tasks that the user is interested in as well as the ability to search and filter all tasks open.

- **GET /search/all** -> Fetches summary for all tasks

##Task Page:
Page that shows the detailed view of a specific task as well as the actions the user can make on it

- **POST /tasks/create** -> Creates a new task for the logged in user
- **POST /tasks/:id/edit** -> Edits a user's task information
- **POST /tasks/:id/apply** -> Apply the user to a specific task
- **POST /tasks/:id/review** -> Give a review for a specific task
- **POST /tasks/:id/cancel** -> Cancel a specific task

- **GET /tasks/:id/view** -> Fetches a detailed view (depending on logged in user) for a specific task
- **GET /tasks/:id/offers** -> Fetches all the offers made to a specific task that the logged in user is authorized to see

##Profile Page:
Page that shows the detailed view of a user profile and the actions that can be done on it
