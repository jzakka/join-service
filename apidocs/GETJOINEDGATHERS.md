**Get Joined Gathers**
----
Return Joined Gathers

* **URL**

  `/joins/:memberId`

* **Method:**

  `GET` 

* **Headers**
  
  **Required:**
  `Authorization: Bearer {bearerToken}`

* **Success Response:**

    * **Code:** 200 <br />
      **Content:** 
      ```json
      [
        {
          "gatherId": "3fb2ee0c-8296-11ee-8f30-03ecd9df0195",
          "rule": "MEMBER"
        },
        {
          "gatherId": "53d6505e-8296-11ee-94ce-57c54dcd2aa1",
          "rule": "MEMBER"
        },
        {
          "gatherId": "58b6e80e-8296-11ee-ae5b-afdc8e223f99",
          "rule": "MEMBER"
        }
      ]
      ```