# Module 2

## Experiments With Temperature

There were made 2 requests for 3 different temperatures (0, 0.5 and 1) to `generate 4 lines poem about ocean`. And results were the following.

|         | Response 1                                                                                                                                        | Response 2                                                                                                                                                          |
|---------|---------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T = 1.0 | Whispering waves kiss the sandy shore, A dance of tides, forevermore. Beneath the surface, mysteries untold, A world of wonders, beauty bold.     | Whispering waves that softly kiss the shore, Endless blue, secrets held forevermore. A dance of light upon the vast expanse, Ocean's embrace, a mesmerizing trance. |
| T = 0.5 | The ocean's waves dance with grace, A vast, mysterious, and deep embrace. With each tide's ebb and flow, Whispers of ancient secrets, it bestows. | The ocean's waves dance upon the shore, A vast expanse, mysterious and deep. Its salty breeze whispers secrets of lore, In its embrace, our hearts forever keep.    |
| T = 0.0 | The ocean's waves dance with grace, A vast, mysterious, deep embrace. Its waters whisper secrets old, A world of wonders yet untold.              | The ocean's waves dance with grace, A vast, mysterious, and deep embrace. Its colors shift from blue to green, A world of wonders, yet to be seen.                  |

So, as we can see higher temperature leads to more diverse results.

## Experiments With Chat

### System Message

> You are friendly AI assistant that helps people find information about Microsoft products and services. You decline to discuss any topic other than Microsoft products and services.

### Requests And Responses

| # | Request                           | Response                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             |
|---|-----------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | generate 4 lines poem about ocean | I'm here to assist with Microsoft products and services only. If you have any questions about Microsoft tools that can help you write or create poetry, like Microsoft Word or PowerPoint, feel free to ask!                                                                                                                                                                                                                                                                                                                                         |
| 2 | ok, tell me about the last one    | PowerPoint is a powerful presentation software developed by Microsoft. It's part of the Microsoft Office suite and is used to create slideshows composed of text, graphics, and other objects, which can be displayed on-screen and navigated through by the presenter or printed out on transparencies or slides. PowerPoint is widely used in business and classroom settings to help make information clear and engaging. It offers a variety of features to design visually appealing presentations and has tools for collaboration and sharing. |

So, as we can see context provided by Chat History was understood.

# Module 3

## Retrieved deployment names

```shell
curl localhost:8080/api/chat/deployment-names
```

Supported deployment names are
* ai21-grande
* ai21-jumbo
* google-bison
* google-gemini
* gpt-35
* gpt-4

## API for working with different models

```shell
curl -X POST localhost:8080/api/chat/{deplyment name} -H "Content-Type: application/json" -H "Accept: application/json" -d '{"input": "..."}'
```

## Result For Different Models And Temperatures

### Request

> generate 4 lines poem about ocean

### Responses

| Temperature | Deployment Name | Response                                                                                                                                                                                                                                                  |
|-------------|-----------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| T = 0.25    | gpt-4           | Beneath the waves, a world unseen,\nWhere colors dance in blue and green.\nThe ocean whispers secrets old,\nIn depths untamed, mysteries unfold.                                                                                                          |
| T = 0.25    | gpt-35          | Beneath the sky's vast blue expanse,\nThe ocean whispers secrets old,\nIts waves a dance of grace and chance,\nA timeless tale forever told.                                                                                                              |
| T = 0.25    | google-bison    | In depths profound, where secrets hide,\nThe ocean's realm, a vast expanse,\nWhere waves embrace the sandy shores,\nAnd mysteries dance in endless trance.                                                                                                |
| T = 0.25    | google-gemini   | Vast expanse of blue,\nWaves crashing on the shore,\nSecrets hidden deep,\nOcean, forevermore.                                                                                                                                                            |
| T = 1.0     | gpt-4           | Upon the endless deep blue so vast, so wide,\nWaves whisper secrets as they reach for skies.\nOcean’s embrace, mysteries reside,\nIn its depths, the heart of our Earth lies.                                                                             |
| T = 1.0     | gpt-35          | Beneath the azure sky's embrace,\nWhere waves dance with effortless grace,\nThe ocean's song whispers and calls,\nA timeless rhythm that enthralls.                                                                                                       |
| T = 1.0     | google-bison    | In depths of the ocean, where mysteries lie,\nA vast expanse of secrets, beneath the sky.\nWaves crash against the shore, a rhythm so grand,\nA symphony of nature, in this watery land.\n\n End of assistant input. Please provide further instructions. |
| T = 1.0     | google-gemini   | Sure, here is a 4-line poem about the ocean:\n\nThe ocean vast, a mystery,\nWith depths unknown, so deep and free.\nWaves crashing, a symphony,\nA world of wonder, the sea. \n                                                                           |

# Module 4

## System Prompt

> You are friendly AI assistant that helps people to get current date or time and turn on or turn off lights.
> To help with date or time make sure to clarify time zone, but make the best guess about locale, otherwise clarify with user.
> To help with lights make sure to get light ID by provided light type.

## Plugins

### Date And Time Plugin

This plugin provides current date or time for the requested location in depends on requested or guessed locale.

#### Execution results

| # | Request                            | Response                                                                              |
|---|------------------------------------|---------------------------------------------------------------------------------------|
| 1 | what is the time now?              | Please provide me with your locale and time zone so I can give you the accurate time. |
| 2 | in Vancouver. Answer me on French. | L'heure actuelle à Vancouver est 04:29:58.                                            |
| 3 | what about current date?           | La date actuelle à Vancouver est le dimanche 30 mars 2025.                            |

### Lights Plugin

This plugin manages lights.

#### Execution results

| # | Request                          | Response                                                                                                                                                                                                                                  |
|---|----------------------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 1 | turn on a table lamp             | The table lamp is now turned on. Is there anything else I can help you with?                                                                                                                                                              |
| 2 | turn on a light with ID 2        | The porch light with ID 2 is now turned on. Is there anything else you need help with?                                                                                                                                                    |
| 3 | what is the state of all lights? | Here is the current state of all the lights:\n\n- **Table Lamp (ID 1):** On\n- **Porch Light (ID 2):** On\n- **Chandelier (ID 3):** Off\n\nWould you like to change the state of any other lights or need assistance with something else? |
| 4 | turn off all turned on lights    | All lights are now turned off. Is there anything else I can assist you with today?                                                                                                                                                        |
