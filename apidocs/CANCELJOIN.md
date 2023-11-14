**Cancel Join**
----
Cancel joining gather

* **URL**

  `/joins`

* **Method:**

  `DELETE`

* **Headers**

  **Required:**
  `Authorization: Bearer {bearerToken}`

* **Data Params**

  **Required:**

  ```json
  {
    "gatherId": "58b6e80e-8296-11ee-ae5b-afdc8e223f99",
    "memberId": "3e9d44ee-8297-11ee-8c0b-5ba8c1c0e154"
  }
  ```


* **Success Response:**

    * **Code:** 200
      