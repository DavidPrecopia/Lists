package com.example.david.lists.view.addedit.item.buildlogic;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.common.buildlogic.ViewCommonModule;
import com.example.david.lists.common.buildlogic.ViewScope;
import com.example.david.lists.view.addedit.common.buildlogic.AddEditDialogCommonModule;
import com.example.david.lists.view.addedit.item.AddEditItemDialog;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.ID;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.TITLE;
import static com.example.david.lists.view.addedit.common.buildlogic.AddEditNamedConstants.USER_LIST_ID;

@ViewScope
@Component(modules = {
        AddEditItemDialogModule.class,
        AddEditDialogCommonModule.class,
        ViewCommonModule.class
})
public interface AddEditItemDialogComponent {
    void inject(AddEditItemDialog fragment);

    @Component.Builder
    interface Builder {
        AddEditItemDialogComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);

        @BindsInstance
        Builder userListId(@Named(USER_LIST_ID) String userListId);
    }
}