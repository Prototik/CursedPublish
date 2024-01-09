# Cursed Publish Plugin Changelog

{{#releases}}
## [{{name}}](https://github.com/Prototik/CursedPublish/releases/tag/{{name}}) ({{date}})

{{#sections}}
### {{name}}

{{#commits}}
* [{{#short7}}{{sha}}{{/short7}}](https://github.com/Prototik/CursedPublish/commit/{{sha}}) {{message.shortMessage}} ({{authorAction.identity.name}})

{{/commits}}
{{^commits}}
No changes.
{{/commits}}
{{/sections}}
{{^sections}}
No changes.
{{/sections}}
{{/releases}}
{{^releases}}
No releases.
{{/releases}}