**Change Time**
----
모임 참여 가능한 시간 수 

* **URL**

  `/joins`

* **Method:**

  `PATCH`

* **Headers**

  **Required:**
  `Authorization: Bearer {bearerToken}`

* **Data Params**

  **Required:**

  ```json
  {
    "gatherId": "6208bb08-8232-11ee-bf8f-773ff4aab5eb",
    "gatherName": "test-gather",
    "email": "test@test.com",
    "memberId": "059d43a8-8231-11ee-af5c-d3a47625207d",
    "rule": "MEMBER",
    "selectDateTimes": [
      { "startDateTime": "2077-10-09T13:20:00", "endDateTime": "2077-10-09T15:00:00" },
      { "startDateTime": "2077-10-11T16:10:00", "endDateTime": "2077-10-11T18:00:00" }
    ]
  }
  ```

* **Success Response:**

    * **Code:** 200
      **Content:**
    
      ```json
      {
        "gatherId": "6208bb08-8232-11ee-bf8f-773ff4aab5eb",
        "gatherName": "test-gather",
        "email": "test@test.com",
        "memberId": "059d43a8-8231-11ee-af5c-d3a47625207d",
        "rule": "MEMBER",
        "selectDateTimes": [
          { "startDateTime": "2077-10-09T13:20:00", "endDateTime": "2077-10-09T15:00:00" },
          { "startDateTime": "2077-10-11T16:10:00", "endDateTime": "2077-10-11T18:00:00" }
        ]
      }
      ```
      