#{if  flash.get((_prefix!=null?_prefix:'') + 'success')}
<div class="alert alert-success">
    &{flash.get((_prefix!=null?_prefix:'') + 'success')}
</div>
#{/if}
#{if  flash.get((_prefix!=null?_prefix:'') + 'warning')}
<div class="alert alert-warning">
    &{flash.get((_prefix!=null?_prefix:'') + 'warning')}
</div>
#{/if}
#{if  flash.get((_prefix!=null?_prefix:'') + 'info')}
<div class="alert alert-info">
&{flash.get((_prefix!=null?_prefix:'') + 'info')}
</div>
#{/if}
#{if  flash.get((_prefix!=null?_prefix:'') + 'error')}
<div class="alert alert-danger">
    &{flash.get((_prefix!=null?_prefix:'') + 'error')}
</div>
#{/if}