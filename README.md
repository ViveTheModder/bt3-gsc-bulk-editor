# bt3-gsc-bulk-editor
A modding tool that reads &amp; writes basic information for all enemies throughout Dragon History scenarios. 

Made solely as a **proof of concept** (which can be used **for other GSC editor tools** in the future), as well as to **assist with Sparking! HYPER** improvement.

That is why it can **only overwrite the Strategy Z-Item ID and the COM Difficulty Level** of each opponent.

These parameters **don't really mean anything for playable characters** of the scenarios.

# Demonstration
## Reading
![demo-1](https://github.com/user-attachments/assets/720a8f95-fe7f-443a-9cc0-f4f896d5cb1b)

![demo-2](https://github.com/user-attachments/assets/c616a509-0553-4134-8bb7-37b72f4e65c8)

![demo-3](https://github.com/user-attachments/assets/e926f703-264d-4d17-afb7-0efd838f2841)

## Writing
![demo-4](https://github.com/user-attachments/assets/7a9296bb-2db9-4ec8-9c8a-ac017388b508)

Here are the CSV files that I overwrote before running the tool.

![demo-5](https://github.com/user-attachments/assets/1c570d62-d4ae-4cf0-bf38-02aed3e6e307)

![demo-6](https://github.com/user-attachments/assets/0c317258-458a-4749-860c-29dd429ed7c4)

![demo-7](https://github.com/user-attachments/assets/3240db3c-09a2-4670-a25b-841a306d567a)

Here is what [Swag Studio](https://github.com/ViveTheModder/swag-studio) displays after the GSC files are overwritten by the GSC Bulk Editor.

![demo-8](https://github.com/user-attachments/assets/01963118-194f-4b4d-b141-089fbdef2e3c)

![demo-9](https://github.com/user-attachments/assets/d4f0cf19-e6e8-47a5-a68e-b7c1dfe75ce6)

![demo-10](https://github.com/user-attachments/assets/22d50a47-a6c9-49d5-9a54-461a0a551597)

Here is a comparison of the original GSC files (bottom) and the modified ones (top).

As we can see, byte 8 changed from ``D0`` to ``E0``, which indicates that the **GSCF size has increased by 16 bytes**.

![demo-11](https://github.com/user-attachments/assets/66c6b4f9-e6e0-4513-b77a-ac85741c0532)

The selected bytes contain the **indices and data types of the parameters that were changed**. For context, think of the GSDT as a **byte array that stores both integers and floats**.

The ``0A`` before each index (which is a short) is a **data type indicator**, which tells us that **each parameter is an integer**. 

As for the index/offset, the one for the COM difficulty level has changed from ``0x1F`` to ``0x3401``. Why?

![demo-12](https://github.com/user-attachments/assets/ea01f8f7-eb3e-46d8-8488-c5105256f17e)

Because **new data has been added to the GSDT**, which it will always add **if said data cannot be found in the GSDT** to begin with.

The program first checks if the **GSDT is fully occupied**. In this case, it is, which is why **it adds 16 more bytes** to the GSDT.

![demo-13](https://github.com/user-attachments/assets/7580e229-0168-49c9-8747-f2393cddea5d)

This is also why the **GSDT size has been updated** (as shown in the header, it went from ``0xD004`` to ``0xE004``) **alongside the GSCF** that was mentioned earlier.

![demo-14](https://github.com/user-attachments/assets/16aede93-535e-4a4e-b550-e4b695ddbe4e)

For a better explanation on **GSDT**, **GSCF**, and all the other GSC jargon, [click here](https://vivethemodder.github.io/complete-gsc-breakdown/).
